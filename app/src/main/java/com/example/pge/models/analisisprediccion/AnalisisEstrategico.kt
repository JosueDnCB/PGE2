package com.example.pge.models.analisisprediccion

import com.google.gson.annotations.SerializedName

data class IaAnalisisResponse(
    val status: String,
    @SerializedName("analisis_ia") val analisisIa: AnalisisIaContent
)

data class AnalisisIaContent(
    val titulo: String,
    @SerializedName("resumen_ejecutivo") val resumenEjecutivo: String,
    @SerializedName("nivel_riesgo_presupuestal") val nivelRiesgo: String, // "BAJO", "MEDIO", "ALTO"
    @SerializedName("acciones_estrategicas") val acciones: List<AccionEstrategica>
)

data class AccionEstrategica(
    val accion: String,
    @SerializedName("plazo_implementacion") val plazo: String,
    val descripcion: String
)