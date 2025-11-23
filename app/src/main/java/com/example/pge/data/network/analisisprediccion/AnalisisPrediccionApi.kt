package com.example.pge.data.network.analisisprediccion

import android.content.Context
import com.example.pge.data.preferences.TokenManager // Asegúrate de importar tu TokenManager
import com.example.pge.data.network.AuthInterceptor // Asegúrate de importar tu AuthInterceptor existente
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.pge.models.analisisprediccion.ProyeccionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrediccionApiService {
    @GET("prediccion/proyeccion-matematica")
    suspend fun obtenerProyeccion(
        @Query("meses") meses: Int,
        @Query("ver_todo_historial") verTodo: Boolean,
        @Query("dependencia_id") dependenciaId: Int? = null
    ): ProyeccionResponse
}

object RetrofitClientAnalisis {

    // URL específica del servicio de Python (Puerto 8001)
    // IMPORTANTE: Si estás en emulador y el python corre en tu PC, usa 10.0.2.2
    // Si usas dispositivo físico, usa tu IP local (ej. 192.168.1.65)
    private const val BASE_URL_FASTAPI = "http://192.168.1.65:8001/" // apis de analisis y predicción

    fun getService(context: Context): PrediccionApiService {

        //  Instanciamos el TokenManager existente
        val tokenManager = TokenManager(context)

        // Configuración de Logs
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Cliente OkHttp reutilizando el interceptor de AuthInterceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Construimos Retrofit apuntando al servicio de Python
        return Retrofit.Builder()
            .baseUrl(BASE_URL_FASTAPI)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrediccionApiService::class.java)
    }
}

