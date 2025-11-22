package com.example.applionscuts.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments WHERE userId = :uid ORDER BY date ASC")
    fun getAppointmentsByUser(uid: Int): Flow<List<AppointmentEntity>>
}
