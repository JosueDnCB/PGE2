package com.example.pge.models

import com.google.gson.annotations.SerializedName

data class Dependencia(
    @SerializedName("id_dependencia")
    val id: Int,

    @SerializedName("nombre_dependencia")
    val nombre: String,

    @SerializedName("edificios_count")
    val edificios: Int,

    // Mapeamos sector_id que sí viene en el JSON
    @SerializedName("sector_id")
    val sectorId: Int? = null
) {
    // Propiedad calculada para la vista.
    // Como el JSON no trae "categoria", la inventamos basándonos en el sector_id o ponemos un default.
    val categoria: String
        get() = "General" // O puedes poner: if (sectorId != null) "Sector $sectorId" else "Sin categoría"
}
