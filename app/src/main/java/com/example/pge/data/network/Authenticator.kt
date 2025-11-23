package com.example.pge.data.network

import okhttp3.Interceptor
import okhttp3.Response
import com.example.pge.data.preferences.TokenManager

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        val requestBuilder = chain.request().newBuilder()
        
        // Headers necesarios para Laravel
        requestBuilder.addHeader("Accept", "application/json")
        
        // IMPORTANTE: Para Docker/Nginx desde el emulador
        // Forzamos el Host a localhost para que Nginx resuelva correctamente la ruta
        requestBuilder.addHeader("Host", "127.0.0.1:8000")

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}