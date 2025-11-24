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

    private val _uiState = MutableStateFlow<AnalisisUiState>(AnalisisUiState.Loading)
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    private val _iaUiState = MutableStateFlow<IaUiState>(IaUiState.Idle)
    val iaUiState: StateFlow<IaUiState> = _iaUiState.asStateFlow()


    //  Usamos getRetrofit(getApplication()) pasando el contexto de la aplicación.
    // getApplication() ya retorna el contexto global, es seguro contra fugas de memoria.
    private val prediccionApi = RetrofitInstanceFastApi.getRetrofit(getApplication()).create(PrediccionApiService::class.java)

    private var mesesProyeccion = 6
    private var verHistorial = false

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = AnalisisUiState.Loading
            try {
                val response = prediccionApi.obtenerProyeccion(
                    meses = mesesProyeccion,
                    verTodo = verHistorial
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
        cargarDatos()
    }
}