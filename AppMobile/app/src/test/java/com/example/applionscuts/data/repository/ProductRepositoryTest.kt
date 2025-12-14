package com.example.applionscuts.data.repository

import com.example.applionscuts.data.local.product.ProductDao
import com.example.applionscuts.data.remote.ProductoApi
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ProductRepositoryTest {

    private lateinit var repository: ProductRepository
    private val productDao: ProductDao = mock()

    @Before
    fun setUp() {
        repository = ProductRepository(productDao)
    }

    @Test
    fun productRepository_instanciaCorrecta() {
        assertNotNull(repository)
    }
}
