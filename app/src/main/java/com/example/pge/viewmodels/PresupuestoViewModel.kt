package com.example.pge.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.data.network.Presupuesto.PresupuestoApi // Tu interfaz creada arriba
import com.example.pge.models.Presupuesto.CreatePresupuestoRequest
import com.example.pge.models.Dependencia
import com.example.pge.models.Presupuesto.Presupuesto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class PresupuestoViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitInstance.getRetrofit(application).create(PresupuestoApi::class.java)

    // --- ESTADOS ---
    private val _dependencias = MutableStateFlow<List<Dependencia>>(emptyList())
    val dependencias = _dependencias.asStateFlow()

    private val _presupuestos = MutableStateFlow<List<Presupuesto>>(emptyList())
    // Esta lista es la que observará la UI (ya filtrada por año)
    val presupuestosFiltrados = MutableStateFlow<List<Presupuesto>>(emptyList())

    // Selecciones
    var dependenciaSeleccionada by mutableStateOf<Dependencia?>(null)
    var anioSeleccionado by mutableStateOf(Calendar.getInstance().get(Calendar.YEAR).toString())

    // KPIs Calculados
    var totalPresupuesto by mutableStateOf(0.0)
    var promedioPresupuesto by mutableStateOf(0.0)

    // Años disponibles (puedes generarlo dinámicamente)
    val aniosDisponibles = (2023..2030).map { it.toString() }

    init {
        cargarDependencias()
    }

    private fun cargarDependencias() {
        viewModelScope.launch {
            try {
                val lista = api.getMisDependencias()
                _dependencias.value = lista

                // Lógica: Autoseleccionar la primera
                if (lista.isNotEmpty()) {
                    seleccionarDependencia(lista[0])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun seleccionarDependencia(dep: Dependencia) {
        dependenciaSeleccionada = dep
        cargarPresupuestos(dep.id)
    }

    fun cambiarAnio(anio: String) {
        anioSeleccionado = anio
        filtrarYCalcular()
    }

    private fun cargarPresupuestos(idDependencia: Int) {
        viewModelScope.launch {
            try {
                // Obtenemos TODOS los presupuestos históricos de esa dependencia
                val lista = api.getPresupuestos(idDependencia)
                _presupuestos.value = lista

                // Filtramos por el año seleccionado actualmente
                filtrarYCalcular()
            } catch (e: Exception) {
                e.printStackTrace()
                _presupuestos.value = emptyList()
                filtrarYCalcular()
            }
        }
    }

    private fun filtrarYCalcular() {
        // Filtro Cliente: La API devuelve todo, aquí filtramos por año
        val filtrados = _presupuestos.value.filter { it.anio.toString() == anioSeleccionado }

        presupuestosFiltrados.value = filtrados

        // Recalcular KPIs
        totalPresupuesto = filtrados.sumOf { it.monto }
        promedioPresupuesto = if (filtrados.isNotEmpty()) totalPresupuesto / filtrados.size else 0.0
    }

    fun crearPresupuesto(anio: String, trimestreStr: String, monto: String, onSuccess: () -> Unit) {
        val depId = dependenciaSeleccionada?.id ?: return

        // Mapeo simple de "Q1..." a int
        val trimInt = when {
            trimestreStr.contains("Q1") -> 1
            trimestreStr.contains("Q2") -> 2
            trimestreStr.contains("Q3") -> 3
            else -> 4
        }

        val request = CreatePresupuestoRequest(
            anio = anio.toInt(),
            trimestre = trimInt,
            monto = monto.toDoubleOrNull() ?: 0.0
        )

        viewModelScope.launch {
            try {
                val response = api.createPresupuesto(depId, request)
                if (response.isSuccessful) {
                    // Recargar la lista para ver el nuevo item
                    cargarPresupuestos(depId)
                    onSuccess()
                    Toast.makeText(getApplication(), "Presupuesto asignado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}