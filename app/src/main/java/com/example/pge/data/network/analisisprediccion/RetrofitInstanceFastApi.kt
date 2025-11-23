package com.example.pge.data.network.analisisprediccion

import android.content.Context
import com.example.pge.data.preferences.TokenManager
import com.example.pge.data.network.AuthenticatorFastApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstanceFastApi {

    // URL para emulador // PC puerto 8001
    private const val BASE_URL_FASTAPI = "http://10.0.2.2:8001/"

    fun getRetrofit(context: Context): Retrofit  {

        val tokenManager = TokenManager(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Cliente OkHttp usando el interceptor ESPECÍFICO de FastAPI
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthenticatorFastApi(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_FASTAPI)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

