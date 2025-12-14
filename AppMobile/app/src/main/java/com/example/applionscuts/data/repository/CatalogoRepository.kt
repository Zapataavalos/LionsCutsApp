package com.example.applionscuts.data.repository

import com.example.applionscuts.data.remote.RemoteModule
import com.example.applionscuts.data.remote.dto.Producto

class CatalogoRepository {

    // 🔹 NOMBRE CORRECTO DEL API
    private val api = RemoteModule.productoApi

    /**
     * Obtiene TODOS los productos
     * GET /api/productos
     */
    suspend fun obtenerTodos(): List<Producto> {
        return api.listarTodos()
    }

    /**
     * Obtiene SOLO productos activos
     * GET /api/productos/activos
     */
    suspend fun obtenerActivos(): List<Producto> {
        return api.listarActivos()
    }
}

