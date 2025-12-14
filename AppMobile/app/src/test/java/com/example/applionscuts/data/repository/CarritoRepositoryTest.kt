package com.example.applionscuts.data.repository

import com.example.applionscuts.data.remote.CarritoApi
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class CarritoRepositoryTest {

    private lateinit var repository: CarritoRepository
    private val carritoApi: CarritoApi = mock()

    @Before
    fun setUp() {
        repository = CarritoRepository(carritoApi)
    }

    @Test
    fun carritoRepository_instanciaCorrecta() {
        assertNotNull(repository)
    }
}
