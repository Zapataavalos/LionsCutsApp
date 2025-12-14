package com.example.applionscuts.data.remote.dto

data class Cita(
    val id: Long? = null,
    val clienteId: Long,
    val productoId: Long,
    val fechaHora: String
)

