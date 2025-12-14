package com.example.applionscuts.data.repository

import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CitasRepositoryTest {

    private lateinit var repository: CitasRepository

    @Before
    fun setUp() {
        repository = CitasRepository()
    }

    @Test
    fun citasRepository_instanciaCorrecta() {
        assertNotNull(repository)
    }
}
