package com.example.pge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.analisisprediccion.PrediccionApiService
import com.example.pge.data.network.analisisprediccion.RetrofitClientAnalisis
import com.example.pge.models.analisisprediccion.ProyeccionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalisisUiState {
    object Loading : AnalisisUiState()
    data class Success(val data: ProyeccionResponse) : AnalisisUiState()
    data class Error(val message: String) : AnalisisUiState()
}

class AnalisisViewModel(private val apiService: PrediccionApiService) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalisisUiState>(AnalisisUiState.Loading)
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    private var mesesProyeccion = 6
    private var verHistorial = false

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = AnalisisUiState.Loading
            try {
                // CORRECCIÓN: Usar 'apiService' (la variable del constructor)
                // en lugar de 'RetrofitClientAnalisis.apiService'.
                val response = apiService.obtenerProyeccion(
                    meses = mesesProyeccion,
                    verTodo = verHistorial
                )

                _uiState.value = AnalisisUiState.Success(response)
            } catch (e: Exception) {
                // Tip: e.localizedMessage a veces es null, mejor usar un fallback
                _uiState.value = AnalisisUiState.Error("Error: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    fun actualizarFiltros(meses: Int) {
        mesesProyeccion = meses
        cargarDatos()
    }
}

class AnalisisViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalisisViewModel::class.java)) {
            // Aquí creamos el servicio pasando el contexto
            val service = RetrofitClientAnalisis.getService(context)
            @Suppress("UNCHECKED_CAST")
            return AnalisisViewModel(service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}