package com.example.applionscuts.data.local.purchase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PurchaseDao {

    @Insert
    suspend fun insertPurchase(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases ORDER BY id DESC")
    suspend fun getAllPurchases(): List<PurchaseEntity>

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY id DESC")
    suspend fun getPurchasesByUserId(userId: Int): List<PurchaseEntity>
}

