package com.example.applionscuts.data.remote.dto

data class Pedido(
    val id: Int,
    val usuarioId: Int,
    val total: Double,
    val productos: List<Producto>
)
