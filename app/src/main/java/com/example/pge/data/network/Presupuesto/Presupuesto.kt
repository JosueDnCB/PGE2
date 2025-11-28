package com.example.pge.data.network.Presupuesto

import com.example.pge.models.Presupuesto.CreatePresupuestoRequest
import com.example.pge.models.Dependencia
import com.example.pge.models.Presupuesto.Presupuesto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PresupuestoApi {
    // Obtener las dependencias del usuario logueado
    @GET("/api/dependencias") // Asumo que esta ruta existe por el JSON que mostraste
    suspend fun getMisDependencias(): List<Dependencia>

    // Ver presupuestos de una dependencia
    @GET("/api/dependencias/{id}/presupuestos")
    suspend fun getPresupuestos(@Path("id") dependenciaId: Int): List<Presupuesto>

    // Asignar presupuesto
    @POST("/api/dependencias/{id}/presupuestos")
    suspend fun createPresupuesto(
        @Path("id") dependenciaId: Int,
        @Body request: CreatePresupuestoRequest
    ): Response<Presupuesto>
}