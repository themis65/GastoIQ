package com.example.gastoiq.network

import com.example.gastoiq.model.Gasto
import com.example.gastoiq.model.Presupuesto
import com.example.gastoiq.model.MetaAhorro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("gastos")
    suspend fun getGastosNube(): Response<List<Gasto>>

    @POST("gastos")
    suspend fun subirGasto(@Body gasto: Gasto): Response<Gasto>

    @GET("presupuestos")
    suspend fun getPresupuestosNube(): Response<List<Presupuesto>>

    @POST("presupuestos")
    suspend fun subirPresupuesto(@Body presupuesto: Presupuesto): Response<Presupuesto>

    @GET("metas")
    suspend fun getMetasNube(): Response<List<MetaAhorro>>

    @POST("metas")
    suspend fun subirMeta(@Body meta: MetaAhorro): Response<MetaAhorro>
}
