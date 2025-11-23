package com.example.pge.data.network

import com.example.pge.models.LoginRequest
import com.example.pge.models.LoginResponse
import com.example.pge.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface LoginApi {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getUser(): Response<UserResponse>

}
