package com.example.pge.models.analisisprediccion
import com.google.gson.annotations.SerializedName

// Respuesta principal de la API
data class ProyeccionResponse(
    val status: String,
    val metodo: String,
    @SerializedName("resumen_proyeccion") val resumen: ResumenProyeccion,
    @SerializedName("datos_para_grafica") val datosGrafica: List<DatoGrafica>
)

data class ResumenProyeccion(
    @SerializedName("horizonte_meses") val horizonteMeses: Int,
    @SerializedName("tendencia_detectada") val tendencia: String,
    @SerializedName("suma_total_costo_proyectada") val costoTotalProyectado: Double,
    @SerializedName("rango_precios_estimado") val rangoEstimado: RangoEstimado
)

data class RangoEstimado(
    val minimo: Double,
    val maximo: Double
)

// Este es el objeto clave para la gráfica (combina histórico y futuro)
data class DatoGrafica(
    val anio: Int,
    val mes: Int,
    @SerializedName("total_kwh") val totalKwh: Double,
    @SerializedName("total_costo") val totalCosto: Double, // Eje Y principal
    val tipo: String, // "real" o "prediccion"
    // Estos campos vendrán null en los datos "real", pero con valor en "prediccion"
    @SerializedName("rango_costo_min") val rangoMin: Double? = null,
    @SerializedName("rango_costo_max") val rangoMax: Double? = null
) {
    // Helper para formatear fecha (Ej: "Feb 26")
    fun obtenerEtiquetaFecha(): String {
        val mesesArr = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
        return "${mesesArr.getOrElse(mes - 1) { "" }} ${anio.toString().takeLast(2)}"
    }
}