package com.example.pge.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.analisisprediccion.PrediccionApiService
import com.example.pge.data.network.analisisprediccion.RetrofitInstanceFastApi
import com.example.pge.models.analisisprediccion.AnalisisIaContent
import com.example.pge.models.analisisprediccion.ProyeccionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define los estados para el analisis predictivo
sealed class AnalisisUiState {
    object Loading : AnalisisUiState()
    data class Success(val data: ProyeccionResponse) : AnalisisUiState()
    data class Error(val message: String) : AnalisisUiState()
}
// Define los estados para el analisis estrategico
sealed class IaUiState {
    object Idle : IaUiState() // Estado inicial (esperando)
    object Loading : IaUiState()
    data class Success(val data: AnalisisIaContent) : IaUiState()
    data class Error(val message: String) : IaUiState()
}

class AnalisisViewModel(application: Application) : AndroidViewModel(application) {

    // ESTADO PARA EL FILTRO DE RANGO DE FECHAS
    // Estado para saber qué texto mostrar en el Dropdown
    private val _rangoSeleccionado = MutableStateFlow("Últimos 12 meses")
    val rangoSeleccionado = _rangoSeleccionado.asStateFlow()

    // Guarda el valor real que se enviara al servidor
    private var mesesProyeccion: Int = 6

    // ESTADO PARA EL FILTRO DE HISTORIAL
    // Para la UI Texto que se muestra en el Dropdown
    private val _opcionHistorial = MutableStateFlow("Año actual")
    val opcionHistorial = _opcionHistorial.asStateFlow()

    // Para la API (Variable booleana interna)
    private var verHistorial: Boolean = false

    private val _uiState = MutableStateFlow<AnalisisUiState>(AnalisisUiState.Loading)
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    private val _iaUiState = MutableStateFlow<IaUiState>(IaUiState.Idle)
    val iaUiState: StateFlow<IaUiState> = _iaUiState.asStateFlow()

    // Función que llama la vista cuando el usuario elige algo
    fun cambiarRangoFecha(nuevoRangoTexto: String) {

        // Actualizamos lo que ve el usuario en el Dropdown
        _rangoSeleccionado.value = nuevoRangoTexto

        // Traducimos ese texto al número que la API entiende
        mesesProyeccion = when (nuevoRangoTexto) {
            "Últimos 12 meses" -> 12
            "Últimos 6 meses" -> 6
            "Último mes" -> 1
            else -> 12
        }

        // Volvemos a pedir la proyección con el nuevo parametro
        cargarAnalisisProyeccion()
        // Volvemos a pedir las recomendaciones con el nuevo número
        cargarAnalisisEstrategico()
    }

    fun cambiarRangoHistorial(nuevoRangoHistorialTexto: String) {

        // Actualizamos la UI
        _opcionHistorial.value = nuevoRangoHistorialTexto

        // Traducimos Texto -> Boolean (y lo guardamos en verHistorial, NO en mesesProyeccion)
        verHistorial = when (nuevoRangoHistorialTexto) {
            "Todo el historial" -> true
            "Año actual" -> false
            else -> false
        }

        // Volvemos a pedir la proyección con el nuevo parametro
        cargarAnalisisProyeccion()
        // Volvemos a pedir las recomendaciones con el nuevo número
        cargarAnalisisEstrategico()
    }

    //  Usamos getRetrofit(getApplication()) pasando el contexto de la aplicación.
    // getApplication() ya retorna el contexto global, es seguro contra fugas de memoria.
    private val prediccionApi = RetrofitInstanceFastApi.getRetrofit(getApplication()).create(PrediccionApiService::class.java)



    init {
        cargarAnalisisProyeccion()
    }

    fun cargarAnalisisProyeccion() {
        viewModelScope.launch {
            _uiState.value = AnalisisUiState.Loading
            try {
                val response = prediccionApi.obtenerProyeccion(
                    meses = mesesProyeccion,
                    verTodo = verHistorial,
                    dependenciaId = 1
                )

                _uiState.value = AnalisisUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = AnalisisUiState.Error("Error: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    fun cargarAnalisisEstrategico() {
        viewModelScope.launch {
            _iaUiState.value = IaUiState.Loading
            try {

                val response = prediccionApi.obtenerEstrategia(
                    meses = mesesProyeccion,
                    verTodo = verHistorial,
                    dependenciaId = 1 // usar el ID real de la dependencia seleccionada
                )
                _iaUiState.value = IaUiState.Success(response.analisisIa)
            } catch (e: Exception) {
                _iaUiState.value = IaUiState.Error("Error IA: ${e.message}")
            }
        }
    }


    fun actualizarFiltros(meses: Int) {
        mesesProyeccion = meses
        cargarAnalisisProyeccion()
    }
}