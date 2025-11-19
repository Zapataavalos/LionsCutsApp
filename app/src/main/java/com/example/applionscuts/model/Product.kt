package com.example.applionscuts.data.local.product

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val brand: String = "LionsCuts",
    val price: Double,
    val description: String = "",
    val longDescription: String = "",
    @DrawableRes val imageResId: Int = 0,
    val stock: Int = 0
)