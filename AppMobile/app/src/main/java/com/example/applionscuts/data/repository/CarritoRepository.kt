package com.example.applionscuts.data.repository


import com.example.applionscuts.data.remote.CarritoApi
import com.example.applionscuts.data.remote.RemoteModule
import com.example.applionscuts.data.remote.dto.Carrito
import com.example.applionscuts.data.remote.dto.ItemCarrito
import com.example.applionscuts.data.remote.dto.ItemRequest
import com.example.applionscuts.data.remote.dto.Pedido
import retrofit2.Response

class CarritoRepository(carritoApi1: CarritoApi) {

    private val carritoApi = RemoteModule.carritoApi

    /**
     * Obtiene el carrito activo del cliente.
     * GET /api/carritos/{clienteId}
     */
    suspend fun obtenerCarritoActivo(clienteId: Long): Carrito {
        return carritoApi.obtenerCarritoActivo(clienteId)
    }

    /**
     * Agrega o actualiza un ítem en el carrito.
     * POST /api/carritos/agregar/{clienteId}
     */
    suspend fun agregarItem(
        clienteId: Long,
        itemRequest: ItemRequest
    ): Response<ItemCarrito> {
        return carritoApi.agregarItem(clienteId, itemRequest)
    }

    /**
     * Elimina un ítem del carrito.
     * DELETE /api/carritos/eliminar/{clienteId}/{productoId}
     */
    suspend fun eliminarItem(
        clienteId: Long,
        productoId: Long
    ): Response<Unit> {
        return carritoApi.eliminarItem(clienteId, productoId)
    }

    /**
     * Calcula el total monetario del carrito.
     * GET /api/carritos/total/{carritoId}
     */
    suspend fun calcularTotal(carritoId: Long): Double {
        return carritoApi.calcularTotal(carritoId)
    }

    /**
     * Calcula la duración total de los servicios del carrito (en minutos).
     * GET /api/carritos/duracion/{carritoId}
     */
    suspend fun calcularDuracion(carritoId: Long): Int {
        return carritoApi.calcularDuracion(carritoId)
    }

    /**
     * Finaliza la compra y genera un Pedido.
     * POST /api/carritos/finalizar/{clienteId}
     */
    suspend fun finalizarCompra(clienteId: Long): Response<Pedido> {
        return carritoApi.finalizarCompra(clienteId)
    }
}
