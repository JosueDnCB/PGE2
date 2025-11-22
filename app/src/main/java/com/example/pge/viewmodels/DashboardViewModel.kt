package com.example.pge.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.models.DashboardResponse
import com.example.pge.models.RetrofitClient
import kotlinx.coroutines.launch
import java.net.ConnectException

// Definimos los estados posibles de la UI
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {

    // Estado observable por Compose
    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        // Cargar datos al iniciar el ViewModel
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            try {
                // Llamada asíncrona a la API
                val response = RetrofitClient.apiService.getDashboardData()
                uiState = DashboardUiState.Success(response)
            } catch (e: ConnectException) {
                uiState = DashboardUiState.Error("No se pudo conectar al servidor. Verifica tu conexión.")
            } catch (e: Exception) {
                uiState = DashboardUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}