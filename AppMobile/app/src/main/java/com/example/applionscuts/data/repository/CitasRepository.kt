package com.example.applionscuts.data.repository


import com.example.applionscuts.data.remote.RemoteModule
import com.example.applionscuts.data.remote.dto.Cita
import retrofit2.Response

class CitasRepository {

    private val citaApi = RemoteModule.citaApi

    /**
     * Obtiene todas las citas
     * GET /api/citas
     */
    suspend fun listarTodas(): List<Cita> {
        return citaApi.listarTodas()
    }

    /**
     * Obtiene una cita por ID
     * GET /api/citas/{id}
     */
    suspend fun obtenerPorId(id: Long): Cita {
        return citaApi.obtenerPorId(id)
    }

    /**
     * Crea una nueva cita
     * POST /api/citas
     *
     * Devuelve Response para poder manejar:
     *  - 201 CREATED
     *  - 409 CONFLICT (hora ocupada)
     *  - 400 BAD REQUEST (validaciones)
     */
    suspend fun crearCita(cita: Cita): Response<Cita> {
        return citaApi.crearCita(cita)
    }

    /**
     * Actualiza una cita existente
     * PUT /api/citas/{id}
     */
    suspend fun actualizarCita(id: Long, cita: Cita): Response<Cita> {
        return citaApi.actualizarCita(id, cita)
    }

    /**
     * Elimina una cita por ID
     * DELETE /api/citas/{id}
     */
    suspend fun eliminarCita(id: Long): Response<Unit> {
        return citaApi.eliminarCita(id)
    }
}

