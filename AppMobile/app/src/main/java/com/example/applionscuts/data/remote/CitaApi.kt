package com.example.applionscuts.data.remote

import com.example.applionscuts.data.remote.dto.Cita
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface CitaApi {

    @GET("api/citas")
    suspend fun listarTodas(): List<Cita>

    @GET("api/citas/{id}")
    suspend fun obtenerPorId(
        @Path("id") id: Long
    ): Cita

    @POST("api/citas")
    suspend fun crearCita(
        @Body cita: Cita
    ): Response<Cita>

    @PUT("api/citas/{id}")
    suspend fun actualizarCita(
        @Path("id") id: Long,
        @Body cita: Cita
    ): Response<Cita>

    @DELETE("api/citas/{id}")
    suspend fun eliminarCita(
        @Path("id") id: Long
    ): Response<Unit>
}
