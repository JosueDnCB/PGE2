package com.example.pge.data.network

import okhttp3.Interceptor
import okhttp3.Response
import com.example.pge.data.preferences.TokenManager

class AuthenticatorFastApi(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        val requestBuilder = chain.request().newBuilder()

        // Headers estándar para APIs JSON
        requestBuilder.addHeader("Accept", "application/json")

        // NOTA: Aquí NO agregamos el header "Host" forzado.
        // FastAPI (Uvicorn) maneja esto correctamente por sí solo en el puerto 8001.
        //requestBuilder.addHeader("Host", "127.0.0.1:8001")
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}