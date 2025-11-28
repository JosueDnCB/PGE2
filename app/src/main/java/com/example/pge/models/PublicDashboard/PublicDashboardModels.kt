package com.example.pge.models.PublicDashboard


import com.google.gson.annotations.SerializedName

// MODELOS PARA CATÁLOGOS  PUBLICOS
// Para sectores
data class SectorItem(
    val id: Int,
    val nombre: String
) {
    // Sobrescribimos toString para que el Dropdown muestre el nombre y no la dirección de memoria
    override fun toString(): String = nombre
}

data class DependenciaItem(
    val id: Int,
    val nombre: String,
    @SerializedName("sector_id") val sectorId: Int // Mapear "sector_id" del JSON a camelCase
) {
    override fun toString(): String = nombre
}

data class EdificioItem(
    val id: Int,
    val nombre: String,
    @SerializedName("dependencia_id") val dependenciaId: Int
) {
    override fun toString(): String = nombre
}

data class RankingItem(
    val nombre: String,
    val total: Double
)

// MODELOS PARA RESPUESTA COMPARATIVA
data class RespuestaComparativa(
    val titulo: String,
    @SerializedName("eje_x") val ejeX: List<String>, // Etiquetas (Meses o Nombres)
    val series: List<SerieGrafica>,
    @SerializedName("dependencias_involucradas") val dependenciasInvolucradas: List<String>
)

data class SerieGrafica(
    val nombre: String,       // Ej: "Hospital General" o "Total kWh"
    val datos: List<Double>,  // Los valores numéricos para graficar
    val color: String? = null // Puede venir null desde Python
)