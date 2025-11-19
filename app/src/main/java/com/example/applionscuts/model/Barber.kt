package com.example.applionscuts.model

import androidx.annotation.DrawableRes

data class Barber(
    val id: String,
    val name: String,
    val specialty: String,
    @DrawableRes val imageResId: Int
)