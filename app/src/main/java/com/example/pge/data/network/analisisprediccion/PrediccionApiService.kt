package com.example.pge.data.network.analisisprediccion

import com.example.pge.models.analisisprediccion.ProyeccionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrediccionApiService {
    @GET("prediccion/proyeccion-matematica")
    suspend fun obtenerProyeccion(
        @Query("meses") meses: Int,
        @Query("ver_todo_historial") verTodo: Boolean,
        @Query("dependencia_id") dependenciaId: Int? = null
    ): ProyeccionResponse
}
