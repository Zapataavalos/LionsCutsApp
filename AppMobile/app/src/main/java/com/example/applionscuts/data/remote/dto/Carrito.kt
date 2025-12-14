package com.example.applionscuts.data.remote.dto

data class Carrito(
    val id: Long,
    val clienteId: Long,
    val items: List<ItemCarrito>
)
