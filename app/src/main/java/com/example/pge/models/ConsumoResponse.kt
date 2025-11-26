package com.example.pge.models

data class ConsumoErrorDetail(
    val fila: Int?,
    val columna: String?,
    val error: List<String>?,
    val valor_erroneo: String?
)

data class ConsumoResponse(
    val message: String?,
    val details: List<ConsumoErrorDetail>? = null,
    val error: String? = null
)
