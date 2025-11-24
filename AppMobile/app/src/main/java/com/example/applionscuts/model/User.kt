package com.example.applionscuts.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    val name: String?= null,
    val email: String?= null,
    val phone: String?= null,
    val password: String?= null,
    val role: String = "cliente",
    val barberSpecificData: String? = null
)