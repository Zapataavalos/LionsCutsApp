package com.example.applionscuts.data.repository

import com.example.applionscuts.data.local.purchase.PurchaseDao
import com.example.applionscuts.data.local.purchase.PurchaseEntity

class PurchaseRepository(
    private val dao: PurchaseDao   // ← ESTE es el nombre correcto
) {

    suspend fun savePurchase(
        userId: Int,
        userName: String,
        userLastNames: String,
        rut: String,
        cardNumber: String,
        cvv: String,
        deliveryMethod: String,
        address: String?,
        cartJson: String,
        amount: Double
    ) {
        val purchase = PurchaseEntity(
            userId = userId,
            userName = userName,
            userLastNames = userLastNames,
            rut = rut,
            cardNumber = cardNumber,
            cvv = cvv,
            deliveryMethod = deliveryMethod,
            address = address,
            cartJson = cartJson,
            totalAmount = amount
        )

        dao.insertPurchase(purchase)   // ← ANTES tenías "purchaseDao", que NO existe
    }

    suspend fun getPurchasesByUserId(userId: Int): List<PurchaseEntity> {
        return dao.getPurchasesByUserId(userId)   // ← Igual aquí
    }

    suspend fun getAllPurchases(): List<PurchaseEntity> {
        return dao.getAllPurchases()
    }
}
