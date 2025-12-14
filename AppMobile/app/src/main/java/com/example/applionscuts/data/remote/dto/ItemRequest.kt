package com.example.applionscuts.data.remote.dto

data class ItemRequest(
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val duracionUnitarioMinutos: Int? = null
)
