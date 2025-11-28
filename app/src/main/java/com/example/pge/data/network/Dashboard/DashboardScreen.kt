package com.example.pge.data.network.Dashboard

import com.example.pge.models.DashboardResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {

    // Endpoint para obtener el dashboard
    @GET("dashboard")
    suspend fun getDashboardData(
        @Query("dependencia_id") dependenciaId: Int? = null,
        @Query("mes") mes: Int? = null,
        @Query("año") anio: Int? = null
    ): DashboardResponse
}

