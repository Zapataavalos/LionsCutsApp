package com.example.applionscuts.data.remote

import com.example.applionscuts.data.remote.dto.Carrito
import com.example.applionscuts.data.remote.dto.ItemCarrito
import com.example.applionscuts.data.remote.dto.ItemRequest
import com.example.applionscuts.data.remote.dto.Pedido
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarritoApi {

    @GET("api/carritos/{clienteId}")
    suspend fun obtenerCarritoActivo(
        @Path("clienteId") clienteId: Long
    ): Carrito

    @POST("api/carritos/agregar/{clienteId}")
    suspend fun agregarItem(
        @Path("clienteId") clienteId: Long,
        @Body item: ItemRequest
    ): Response<ItemCarrito>

    @DELETE("api/carritos/eliminar/{clienteId}/{productoId}")
    suspend fun eliminarItem(
        @Path("clienteId") clienteId: Long,
        @Path("productoId") productoId: Long
    ): Response<Unit>

    @GET("api/carritos/total/{carritoId}")
    suspend fun calcularTotal(
        @Path("carritoId") carritoId: Long
    ): Double

    @GET("api/carritos/duracion/{carritoId}")
    suspend fun calcularDuracion(
        @Path("carritoId") carritoId: Long
    ): Int

    @POST("api/carritos/finalizar/{clienteId}")
    suspend fun finalizarCompra(
        @Path("clienteId") clienteId: Long
    ): Response<Pedido>
}

