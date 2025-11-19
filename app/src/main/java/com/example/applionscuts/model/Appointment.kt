package com.example.applionscuts.model


data class Appointment(
    val id: String,
    val barberName: String,
    val service: String,
    val date: String,
    val time: String
)