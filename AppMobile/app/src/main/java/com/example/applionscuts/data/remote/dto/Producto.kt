package com.example.applionscuts.data.remote.dto

data class Producto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val activo: Boolean,
    val tipo: TipoProducto
)

