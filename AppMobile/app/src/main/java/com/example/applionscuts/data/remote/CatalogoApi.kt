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

interface CatalogoApi {

    // ---- 1. CREAR PRODUCTO ----
    // POST /api/productos
    @POST("api/productos")
    suspend fun crearProducto(
        @Body producto: Producto
    ): Response<Producto>

    // ---- 2. LISTAR TODOS ----
    // GET /api/productos
    @GET("api/productos")
    suspend fun listarTodos(): List<Producto>

    // ---- 3. OBTENER POR ID ----
    // GET /api/productos/{id}
    @GET("api/productos/{id}")
    suspend fun obtenerPorId(
        @Path("id") id: Long
    ): Producto

    // ---- 4. LISTAR ACTIVOS ----
    // GET /api/productos/activos
    @GET("api/productos/activos")
    suspend fun listarActivos(): List<Producto>

    // ---- 5. LISTAR POR TIPO ----
    // GET /api/productos/tipo/{tipo}
    @GET("api/productos/tipo/{tipo}")
    suspend fun listarPorTipo(
        @Path("tipo") tipo: TipoProducto
    ): List<Producto>

    // ---- 6. ACTUALIZAR PRODUCTO ----
    // PUT /api/productos/{id}
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: Producto
    ): Producto

    // ---- 7. ELIMINAR PRODUCTO ----
    // DELETE /api/productos/{id}
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>

    // ---- 8. CHECK PRODUCTO ACTIVO (INTEGRACIÓN) ----
    // GET /api/productos/check-activo/{id}
    @GET("api/productos/check-activo/{id}")
    suspend fun checkProductoActivo(
        @Path("id") id: Long
    ): Boolean
}
