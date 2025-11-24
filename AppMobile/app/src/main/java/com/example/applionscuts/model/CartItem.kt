package com.example.applionscuts.model

import com.example.applionscuts.data.local.product.Product

data class CartItem(
    val product: Product,
    val quantity: Int
)