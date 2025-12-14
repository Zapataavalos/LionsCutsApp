package com.example.applionscuts.data.repository

import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CatalogoRepositoryTest {

    private lateinit var repository: CatalogoRepository

    @Before
    fun setUp() {
        repository = CatalogoRepository()
    }

    @Test
    fun catalogoRepository_instanciaCorrecta() {
        assertNotNull(repository)
    }
}