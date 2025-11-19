package com.example.applionscuts.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String = "cliente",
    val barberSpecificData: String? = null
)