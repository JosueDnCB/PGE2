package com.example.pge.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.analisisprediccion.PrediccionApiService
import com.example.pge.data.network.analisisprediccion.RetrofitInstanceFastApi
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

class AnalisisViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<AnalisisUiState>(AnalisisUiState.Loading)
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    // 2. Usamos getService pasando el contexto de la aplicación.
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