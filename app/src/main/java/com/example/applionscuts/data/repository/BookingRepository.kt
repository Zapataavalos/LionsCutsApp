package com.example.applionscuts.repository

import com.example.applionscuts.data.local.appointment.AppointmentDao
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val dao: AppointmentDao) {

    suspend fun saveAppointment(appointment: AppointmentEntity) {
        dao.insertAppointment(appointment)
    }

    fun getUserAppointments(userId: Int): Flow<List<AppointmentEntity>> {
        return dao.getAppointmentsByUser(userId)
    }
}
