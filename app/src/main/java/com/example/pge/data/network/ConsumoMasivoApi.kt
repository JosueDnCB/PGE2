package com.example.pge.data.network

import com.example.pge.models.ConsumoResponse
import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ConsumoMasivoApi {
    @Multipart
    @POST("consumos/carga-masiva")
    suspend fun cargarArchivo(
        @Part archivo: MultipartBody.Part
    ): retrofit2.Response<ConsumoResponse>
}