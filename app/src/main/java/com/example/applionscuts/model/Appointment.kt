package com.example.applionscuts.data.local.appointment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val userName: String,
    val barberName: String,
    val service: String,
    val date: String,   // DD/MM/YYYY
    val time: String    // 10:00 AM
)
