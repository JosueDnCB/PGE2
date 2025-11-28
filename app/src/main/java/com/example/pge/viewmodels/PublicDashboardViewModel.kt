package com.example.pge.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.RetrofitInstanceFastApi
import com.example.pge.data.network.PublicDashboard.PublicApiService // Asegúrate de tener este import
import com.example.pge.models.*
import com.example.pge.models.PublicDashboard.DependenciaItem
import com.example.pge.models.PublicDashboard.EdificioItem
import com.example.pge.models.PublicDashboard.RespuestaComparativa
import com.example.pge.models.PublicDashboard.SectorItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class PublicDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitInstanceFastApi.getRetrofit(getApplication()).create(PublicApiService::class.java)

    // ESTADOS DE CONTROL (Carga y Error)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Función para limpiar el error desde la UI (ej. al cerrar el Snackbar)
    fun limpiarError() {
        _errorMessage.value = null
    }

    // ESTADOS DE CATALOGOS
    private val _sectores = MutableStateFlow<List<SectorItem>>(emptyList())
    val sectores = _sectores.asStateFlow()

    private val _dependencias = MutableStateFlow<List<DependenciaItem>>(emptyList())
    val dependencias = _dependencias.asStateFlow()

    private val _edificios = MutableStateFlow<List<EdificioItem>>(emptyList())
    val edificios = _edificios.asStateFlow()

    // SELECCIONES
    val sectorSel = MutableStateFlow<SectorItem?>(null)
    val dependenciaSel = MutableStateFlow<DependenciaItem?>(null)
    val edificioSel = MutableStateFlow<EdificioItem?>(null)

    // ESTADO DEL NIVEL DE COMPARACIÓN
    // Opciones: "Sectores", "Dependencias", "Edificios"
    // Valor inicial: "Sectores" (para comparar Educación vs Salud vs Gobierno, etc.)
    val nivelesDisponibles = listOf("Sectores", "Dependencias", "Edificios")
    val nivelSeleccionado = MutableStateFlow("Sectores")
    val anioSel = MutableStateFlow(2025) // Año por defecto

    // Lista de años disponibles segun los datos de la db (Del 2015 al 2025)
    val aniosDisponibles = (2015..2025).toList().reversed()

    // DATOS DE GRÁFICAS
    private val _comparativaConsumo = MutableStateFlow<RespuestaComparativa?>(null)
    val comparativaConsumo = _comparativaConsumo.asStateFlow()

    private val _comparativaCostos = MutableStateFlow<RespuestaComparativa?>(null)
    val comparativaCostos = _comparativaCostos.asStateFlow()

    private val _ranking = MutableStateFlow<RespuestaComparativa?>(null)
    val ranking = _ranking.asStateFlow()

    private val _presupuestoVsGasto = MutableStateFlow<RespuestaComparativa?>(null)
    val presupuestoVsGasto = _presupuestoVsGasto.asStateFlow()

    init {
        cargarSectores()
    }
    // Función para cambiar el nivel
    fun cambiarNivel(nivel: String) {
        nivelSeleccionado.value = nivel
        // Al cambiar de nivel, limpiamos selecciones inferiores para evitar confusión
        if (nivel == "Sectores") {
            dependenciaSel.value = null
            edificioSel.value = null
        }
        actualizarGraficas()
    }

    private fun cargarSectores() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("API_DEBUG", "Intentando cargar sectores desde: ${RetrofitInstanceFastApi.BASE_URL_FASTAPI}") // Ver URL
                _sectores.value = api.getSectores()
                Log.d("API_DEBUG", "Sectores cargados: ${_sectores.value.size}")
            } catch (e: Exception) {
                // ESTO ES LO IMPORTANTE: Imprimir el error en la consola
                Log.e("API_ERROR", "Error cargando sectores", e)

                e.printStackTrace()
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para cambiar año
    fun seleccionarAnio(anio: Int) {
        anioSel.value = anio
        // Al cambiar el año, recargamos las gráficas con la selección actual
        actualizarGraficas()
    }

    // LÓGICA DE CASCADA

    fun seleccionarSector(sector: SectorItem) {
        sectorSel.value = sector
        // Limpiamos hijos
        dependenciaSel.value = null
        edificioSel.value = null
        _edificios.value = emptyList()

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _dependencias.value = api.getDependencias(sector.id)

                actualizarGraficas()
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al cargar dependencias: ${e.localizedMessage}"
                _isLoading.value = false // Solo apagamos si falló aquí, si no, actualizarGraficas lo maneja
            } finally {
                // No apagamos isLoading aquí si todo salió bien, porque actualizarGraficas seguirá cargando
                if (_errorMessage.value != null) _isLoading.value = false
            }
        }
    }

    fun seleccionarDependencia(dep: DependenciaItem) {
        dependenciaSel.value = dep
        // Limpiamos hijo
        edificioSel.value = null

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _edificios.value = api.getEdificios(dep.id)
               actualizarGraficas()
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al cargar edificios: ${e.localizedMessage}"
                _isLoading.value = false
            } finally {
                if (_errorMessage.value != null) _isLoading.value = false
            }
        }
    }

    fun seleccionarEdificio(edificio: EdificioItem) {
        edificioSel.value = edificio
        actualizarGraficas()
    }

    // ENVIAR TODOS LOS IDS DISPONIBLES
    private fun actualizarGraficas() {
        viewModelScope.launch {
            val nivel = nivelSeleccionado.value
            val anio = anioSel.value

            // Dependiendo del nivel, tomamos la lista COMPLETA de items cargados
            // y concatenamos sus IDs con comas

            val (idsStr, tipoFiltroApi) = when (nivel) {
                "Sectores" -> {
                    // Enviamos TODOS los sectores disponibles
                    val todosLosIds = _sectores.value.joinToString(",") { it.id.toString() }
                    Pair(todosLosIds, "sector")
                }
                "Dependencias" -> {
                    // Enviamos TODAS las dependencias que estén cargadas actualmente (del sector seleccionado)
                    // Si no hay dependencias cargadas, enviamos string vacío para evitar error
                    val todosLosIds = _dependencias.value.joinToString(",") { it.id.toString() }
                    Pair(todosLosIds, "dependencia")
                }
                "Edificios" -> {
                    // Enviamos TODOS los edificios que estén cargados actualmente (de la dep seleccionada)
                    val todosLosIds = _edificios.value.joinToString(",") { it.id.toString() }
                    Pair(todosLosIds, "edificio")
                }
                else -> Pair("", "sector")
            }

            // Si no hay IDs, no llamamos
            if (idsStr.isEmpty()) {

                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Log para depurar qué estamos enviando
                Log.d("API_COMPARATIVA", "Consultando: anio=$anio, ids=[$idsStr], tipo=$tipoFiltroApi")

                _comparativaConsumo.value = api.getComparativoConsumo(anio, idsStr, tipoFiltroApi)
                _comparativaCostos.value = api.getComparativoCostos(anio, idsStr, tipoFiltroApi)
                _ranking.value = api.getRanking(anio, idsStr, tipoFiltroApi)
                _presupuestoVsGasto.value = api.getPresupuestoVsGasto(anio, idsStr, tipoFiltroApi)

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar gráficas."
            } finally {
                _isLoading.value = false
            }
        }
    }
}