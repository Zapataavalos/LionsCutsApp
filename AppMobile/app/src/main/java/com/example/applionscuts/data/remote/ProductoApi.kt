package com.example.applionscuts.data.remote

import com.example.applionscuts.data.remote.dto.Producto
import com.example.applionscuts.data.remote.dto.TipoProducto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApi {

    @POST("api/productos")
    suspend fun crearProducto(
        @Body producto: Producto
    ): Response<Producto>

    @GET("api/productos")
    suspend fun listarTodos(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun obtenerPorId(
        @Path("id") id: Long
    ): Producto

    @GET("api/productos/activos")
    suspend fun listarActivos(): List<Producto>

    @GET("api/productos/tipo/{tipo}")
    suspend fun listarPorTipo(
        @Path("tipo") tipo: TipoProducto
    ): List<Producto>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: Producto
    ): Producto

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>

    @GET("api/productos/check-activo/{id}")
    suspend fun checkProductoActivo(
        @Path("id") id: Long
    ): Boolean
}
