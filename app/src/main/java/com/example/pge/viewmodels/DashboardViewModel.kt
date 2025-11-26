package com.example.pge.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.data.network.analisisprediccion.PrediccionApiService
import com.example.pge.data.network.Dashboard.DashboardApi
import com.example.pge.models.DashboardResponse
import kotlinx.coroutines.launch
import java.net.ConnectException

// Definimos los estados posibles de la UI
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    // Estado observable por Compose
    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)

    private val api = RetrofitInstance.getRetrofit(getApplication()).create(DashboardApi::class.java)

    init {
        // Cargar datos al iniciar el ViewModel
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            try {
                // Llamada asíncrona a la API
                val response = api.getDashboardData()
                uiState = DashboardUiState.Success(response)
            } catch (e: ConnectException) {
                uiState = DashboardUiState.Error("No se pudo conectar al servidor. Verifica tu conexión.")
            } catch (e: Exception) {
                uiState = DashboardUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}