package com.example.applionscuts.model

import androidx.annotation.DrawableRes

data class Haircut(
    val id: String,
    val name: String,
    val description: String,
    val longDescription: String,
    val price: Double,
    @DrawableRes val imageResId: Int
)