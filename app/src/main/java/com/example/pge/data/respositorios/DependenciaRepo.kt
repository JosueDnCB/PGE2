package com.example.pge.data.repositorios

import android.content.Context
import com.example.pge.data.network.DependenciasApi
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.models.Dependencia
import retrofit2.Response

class DependenciasRepo(context: Context) {

    private val api = RetrofitInstance.getRetrofit(context).create(DependenciasApi::class.java)

    suspend fun getDependencias(): List<Dependencia> {
        val response = api.getDependencias()

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error API: ${response.code()} - ${response.message()}")
        }
    }
}
