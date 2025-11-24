package com.example.applionscuts.data.repository

import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.data.local.product.ProductDao

class ProductRepository(private val productDao: ProductDao) {
    suspend fun getProducts() = productDao.getAllProducts()
    suspend fun addProduct(product: Product) = productDao.insertProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
}
