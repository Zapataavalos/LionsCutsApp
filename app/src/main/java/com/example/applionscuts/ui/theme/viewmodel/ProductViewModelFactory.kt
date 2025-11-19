package com.example.applionscuts.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.applionscuts.data.local.database.AppDatabase
import com.example.applionscuts.data.local.product.ProductDao
import com.example.applionscuts.data.repository.ProductRepository

class ProductViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            val repo = ProductRepository(productDao)
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}