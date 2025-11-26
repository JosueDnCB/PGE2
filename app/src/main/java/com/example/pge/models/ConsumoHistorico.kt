package com.example.pge.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


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