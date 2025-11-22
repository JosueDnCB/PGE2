package com.example.pge.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- 1. Data Models (Coinciden con tu JSON de Laravel) ---

data class DashboardResponse(
    val status: String,
    val periodo: Periodo,
    val kpis: Kpis,
    val data_evolucion: List<EvolucionItem>,
    val data_inmuebles: List<InmuebleItem>
)

data class Periodo(
    val mes: Int,
    val año: Int,
    val trimestre: Int
)

data class Kpis(
    val consumo_mes_kwh: Double,
    val costo_mes: Double,
    val presupuesto_trimestre: Double,
    val porcentaje_ejecucion: Double? = 0.0
)

data class EvolucionItem(
    val año: Int,
    val mes: Int,
    val total_consumo: Double,
    val total_costo: Double
)

data class InmuebleItem(
    val nombre_edificio: String,
    val consumo: Double // Consumo en kWh
)

// --- 2. Retrofit Interface ---

interface PgeApiService {
    // Endpoint para obtener el dashboard
    // http://127.0.0.1:8000/api/dashboard
    @GET("dashboard")
    suspend fun getDashboardData(
        @Query("dependencia_id") dependenciaId: Int? = null,
        @Query("mes") mes: Int? = null,
        @Query("año") anio: Int? = null
    ): DashboardResponse
}

// --- 3. Singleton de Retrofit (Simple) ---

object RetrofitClient {
    // Nota: 10.0.2.2 es localhost para el emulador de Android.
    // Si usas dispositivo físico, usa la IP de tu PC (ej. 192.168.1.x)
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val apiService: PgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PgeApiService::class.java)
    }
}