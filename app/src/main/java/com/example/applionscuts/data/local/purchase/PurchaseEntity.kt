package com.example.applionscuts.data.local.purchase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val userId: Int,

    val userName: String,
    val userLastNames: String,
    val rut: String,
    val cardNumber: String,
    val cvv: String,
    val deliveryMethod: String,
    val address: String?,
    val cartJson: String,
    val totalAmount: Double
)


