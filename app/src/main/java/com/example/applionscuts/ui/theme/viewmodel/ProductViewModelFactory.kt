package com.example.applionscuts.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.applionscuts.data.local.product.ProductDao
import com.example.applionscuts.data.repository.ProductRepository
import com.example.applionscuts.data.repository.PurchaseRepository

class ProductViewModelFactory(
    private val productDao: ProductDao,
    private val purchaseRepository: PurchaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            val repo = ProductRepository(productDao)
            return ProductViewModel(repo, purchaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
