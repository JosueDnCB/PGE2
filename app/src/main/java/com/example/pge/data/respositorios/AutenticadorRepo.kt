package com.example.pge.data.respositorios

import android.content.Context
import com.example.pge.data.network.LoginApi
import com.example.pge.models.LoginResponse
import com.example.pge.models.LoginRequest
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.models.UserResponse
import retrofit2.Response

class AutenticadorRepo(private val context: Context) {

    private val loginApi = RetrofitInstance.getRetrofit(context).create(LoginApi::class.java)

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return loginApi.login(LoginRequest(email, password))
    }

    suspend fun getUser(): Response<UserResponse> {
        return loginApi.getUser() // ya agrega el token automáticamente
    }
}
