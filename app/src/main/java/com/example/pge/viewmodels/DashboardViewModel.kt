package com.example.pge.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.data.network.Dashboard.DashboardApi
import com.example.pge.models.DashboardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.util.Calendar

// Estados UI
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    // 1. OBTENER FECHA ACTUAL DEL SISTEMA
    private val calendar = Calendar.getInstance()
    private val yearActualSistema = calendar.get(Calendar.YEAR)       // Ej: 2025
    private val monthActualSistema = calendar.get(Calendar.MONTH) + 1 // Ej: 11 (Noviembre)

    // Estado UI General
    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)

    // Dependencia
    private var dependenciaIdSeleccionada: Int? = null
    private val _opcionDependencia = MutableStateFlow("Dependencias")
    val opcionDependencia = _opcionDependencia.asStateFlow()

    // 2. ESTADOS DE FECHA (Inician con la fecha actual)
    private val _anioSeleccionado = MutableStateFlow(yearActualSistema)
    val anioSeleccionado = _anioSeleccionado.asStateFlow()

    private val _mesSeleccionado = MutableStateFlow(monthActualSistema)
    val mesSeleccionado = _mesSeleccionado.asStateFlow()

    // Lista de años disponibles (2015 al actual)
    val aniosDisponibles = (2015..yearActualSistema).toList().reversed()

    private val api = RetrofitInstance.getRetrofit(getApplication()).create(DashboardApi::class.java)

    init {
        // La primera carga usará los valores por defecto (Fecha actual y Dependencia null)
        fetchDashboardData()
    }

    // --- FUNCIONES DE CAMBIO ---

    fun cambiarDependencia(id: Int?, nombre: String) {
        _opcionDependencia.value = nombre
        dependenciaIdSeleccionada = id
        fetchDashboardData()
    }

    fun cambiarAnio(anio: Int) {
        _anioSeleccionado.value = anio

        // REGLA DE NEGOCIO: Si cambian al año actual y el mes seleccionado es futuro,
        // regresamos al mes actual. (Ej: Estaba en Dic 2024 -> Cambio a 2025 -> Se ajusta a Nov 2025)
        if (anio == yearActualSistema && _mesSeleccionado.value > monthActualSistema) {
            _mesSeleccionado.value = monthActualSistema
        }

        fetchDashboardData()
    }

    fun cambiarMes(mes: Int) {
        _mesSeleccionado.value = mes
        fetchDashboardData()
    }

    // --- CARGA DE DATOS ---

    fun fetchDashboardData() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            try {
                // Enviamos TODOS los filtros a la API
                val response = api.getDashboardData(
                    dependenciaId = dependenciaIdSeleccionada,
                    anio = _anioSeleccionado.value,
                    mes = _mesSeleccionado.value
                )
                uiState = DashboardUiState.Success(response)
            } catch (e: ConnectException) {
                uiState = DashboardUiState.Error("No se pudo conectar al servidor.")
            } catch (e: Exception) {
                uiState = DashboardUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    // Helper para la UI: Decide qué meses mostrar en el dropdown
    fun obtenerMesesValidos(): List<Int> {
        return if (_anioSeleccionado.value == yearActualSistema) {
            // Si es el año actual, solo mostramos hasta el mes actual
            (1..monthActualSistema).toList()
        } else {
            // Si es un año pasado, mostramos los 12 meses
            (1..12).toList()
        }
    }
}