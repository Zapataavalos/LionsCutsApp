package com.example.applionscuts.data.remote

import com.example.applionscuts.data.remote.dto.Pedido
import retrofit2.http.GET
import retrofit2.http.Path

interface PedidoApi {

    /**
     * ⚠️ NO EXISTE un POST directo para Pedido
     * El pedido se crea desde:
     * POST /api/carritos/finalizar/{clienteId}
     *
     * → Este endpoint se maneja en CarritoApi
     */

    /**
     * Obtiene los pedidos de un cliente
     * ❗ Solo implementa este método si tu backend lo expone más adelante
     *
     * Ejemplo futuro:
     * GET /api/pedidos/cliente/{clienteId}
     */
    @GET("api/pedidos/cliente/{clienteId}")
    suspend fun obtenerPedidosPorCliente(
        @Path("clienteId") clienteId: Long
    ): List<Pedido>
}
