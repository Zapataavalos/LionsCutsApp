package com.example.applionscuts.model

// Contiene los datos básicos del usuario
data class UserProfile(
    val uid: String,
    val name: String?,
    val email: String?,
    val phone: String?,
    val fidelityStars: Int
)

