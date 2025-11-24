package com.example.pge.data.network.analisisprediccion

import com.example.pge.models.analisisprediccion.IaAnalisisResponse
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

    @GET("prediccion/ia-analisis-estrategico")
    suspend fun obtenerEstrategia(
        @Query("meses") meses: Int,
        @Query("ver_todo_historial") verTodo: Boolean,
        @Query("dependencia_id") dependenciaId: Int? = null
    ): IaAnalisisResponse
}
