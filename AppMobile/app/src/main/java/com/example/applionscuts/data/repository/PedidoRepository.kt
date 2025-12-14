package com.example.applionscuts.data.repository

import com.example.applionscuts.data.remote.RemoteModule
import com.example.applionscuts.data.remote.dto.Pedido
import retrofit2.Response

class PedidoRepository {

    private val carritoApi = RemoteModule.carritoApi

    /**
     * Finaliza la compra del carrito activo y genera un Pedido.
     * POST /api/carritos/finalizar/{clienteId}
     */
    suspend fun finalizarCompra(clienteId: Long): Response<Pedido> {
        return carritoApi.finalizarCompra(clienteId)
    }
}
