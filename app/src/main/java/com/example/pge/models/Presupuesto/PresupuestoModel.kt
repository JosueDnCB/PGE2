package com.example.pge.models.Presupuesto


import com.google.gson.annotations.SerializedName

// Modelo para la lista de dependencias
data class Dependencia(
    @SerializedName("id_dependencia") val id: Int,
    @SerializedName("nombre_dependencia") val nombre: String,
    // ... otros campos si los necesitas
) {
    override fun toString(): String = nombre
}

// Modelo para el Presupuesto
data class Presupuesto(
    val id: Int,
    @SerializedName("dependencia_id") val dependenciaId: Int,
    @SerializedName("año") val anio: Int,       // Laravel usa 'año'
    val trimestre: Int,
    @SerializedName("monto_asignado") val monto: Double,
    @SerializedName("created_at") val createdAt: String? = null
)

// DTO para enviar al crear
data class CreatePresupuestoRequest(
    @SerializedName("año") val anio: Int,
    val trimestre: Int,
    @SerializedName("monto_asignado") val monto: Double
)