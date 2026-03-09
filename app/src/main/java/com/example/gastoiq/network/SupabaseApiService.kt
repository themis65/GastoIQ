package com.example.gastoiq.network

import com.example.gastoiq.model.*
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    // ===== GASTOS =====
    @GET("gastos")
    suspend fun getGastos(
        @Query("select") select: String = "*",
        @Query("order") order: String = "fecha.desc"
    ): Response<List<Gasto>>

    @POST("gastos")
    suspend fun insertGasto(@Body gasto: Gasto): Response<List<Gasto>>

    @PATCH("gastos")
    suspend fun updateGasto(
        @Query("id") id: String,
        @Body gasto: Gasto
    ): Response<List<Gasto>>

    @DELETE("gastos")
    suspend fun deleteGasto(@Query("id") id: String): Response<Unit>

    // ===== PRESUPUESTOS =====
    @GET("presupuestos")
    suspend fun getPresupuestos(
        @Query("select") select: String = "*",
        @Query("order") order: String = "monto_limite.desc"
    ): Response<List<Presupuesto>>

    @POST("presupuestos")
    suspend fun insertPresupuesto(@Body presupuesto: Presupuesto): Response<List<Presupuesto>>

    @DELETE("presupuestos")
    suspend fun deletePresupuesto(@Query("id") id: String): Response<Unit>

    // ===== METAS =====
    @GET("metas_ahorro")
    suspend fun getMetas(
        @Query("select") select: String = "*",
        @Query("order") order: String = "fecha_limite.asc"
    ): Response<List<MetaAhorro>>

    @POST("metas_ahorro")
    suspend fun insertMeta(@Body meta: MetaAhorro): Response<List<MetaAhorro>>

    @PATCH("metas_ahorro")
    suspend fun updateMeta(
        @Query("id") id: String,
        @Body meta: MetaAhorro
    ): Response<List<MetaAhorro>>

    @DELETE("metas_ahorro")
    suspend fun deleteMeta(@Query("id") id: String): Response<Unit>

    // ===== CATEGORIAS =====
    @GET("categorias")
    suspend fun getCategorias(@Query("select") select: String = "*"): Response<List<Categoria>>

    // ===== ETIQUETAS =====
    @GET("etiquetas")
    suspend fun getEtiquetas(@Query("select") select: String = "*"): Response<List<Etiqueta>>
}
