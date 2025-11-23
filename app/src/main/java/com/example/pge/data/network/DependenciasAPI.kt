package com.example.pge.data.network

import com.example.pge.models.Dependencia
import retrofit2.Response
import retrofit2.http.GET

interface DependenciasApi {

    @GET("dependencias")
    suspend fun getDependencias(): Response<List<Dependencia>>
}