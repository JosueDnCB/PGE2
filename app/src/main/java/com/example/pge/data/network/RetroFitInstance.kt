package com.example.pge.data.network

import android.content.Context
import com.example.pge.data.preferences.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    fun getRetrofit(context: Context): Retrofit {

        val tokenManager = TokenManager(context)
        
        // Creamos el interceptor para logs
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging) // Agregamos el logging interceptor
            .addInterceptor(AuthInterceptor(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // CONFIRMADO: El puerto es 8000 (ya que el 80 dio timeout).
        // El problema del HTML 404 anterior se debe solucionar con el header "Host: localhost"
        // que ya agregamos en Authenticator.kt.
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") 
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
