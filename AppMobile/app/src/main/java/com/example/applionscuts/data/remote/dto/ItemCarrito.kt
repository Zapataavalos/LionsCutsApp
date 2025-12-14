package com.example.applionscuts.data.remote.dto

data class ItemCarrito(
    val id: Long,
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val duracionUnitarioMinutos: Int?
)
