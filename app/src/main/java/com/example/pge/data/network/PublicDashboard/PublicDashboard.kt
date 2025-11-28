package com.example.pge.data.network.PublicDashboard

import com.example.pge.models.*
import com.example.pge.models.PublicDashboard.DependenciaItem
import com.example.pge.models.PublicDashboard.EdificioItem
import com.example.pge.models.PublicDashboard.RespuestaComparativa
import com.example.pge.models.PublicDashboard.SectorItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicApiService {

    // CATÁLOGOS

    @GET("/catalogos/sectores")
    suspend fun getSectores(): List<SectorItem>

    @GET("/catalogos/dependencias/{sector_id}")
    suspend fun getDependencias(@Path("sector_id") sectorId: Int): List<DependenciaItem>

    @GET("/catalogos/edificios/{dependencia_id}")
    suspend fun getEdificios(@Path("dependencia_id") dependenciaId: Int): List<EdificioItem>


    // ANÁLISIS PÚBLICO

    // Nota: Python espera params: anio (int), ids (string "1,2"), tipo_filtro (string)

    @GET("/analisis/publico/comparativa-consumo")
    suspend fun getComparativoConsumo(
        @Query("anio") anio: Int,
        @Query("ids") ids: String,
        @Query("tipo_filtro") tipoFiltro: String
    ): RespuestaComparativa

    @GET("/analisis/publico/comparativa-costos")
    suspend fun getComparativoCostos(
        @Query("anio") anio: Int,
        @Query("ids") ids: String,
        @Query("tipo_filtro") tipoFiltro: String
    ): RespuestaComparativa

    @GET("/analisis/publico/ranking")
    suspend fun getRanking(
        @Query("anio") anio: Int,
        @Query("ids") ids: String,
        @Query("tipo_filtro") tipoFiltro: String
    ): RespuestaComparativa

    @GET("/analisis/publico/presupuesto-vs-gasto")
    suspend fun getPresupuestoVsGasto(
        @Query("anio") anio: Int,
        @Query("ids") ids: String,
        @Query("tipo_filtro") tipoFiltro: String
    ): RespuestaComparativa
}