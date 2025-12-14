package com.example.applionscuts.domain.validation

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidatorsTest {

    private lateinit var validators: Validators

    @Before
    fun setUp() {
        validators = Validators()
    }

    // ---------------------------------------------------
    // EMAIL
    // ---------------------------------------------------

    @Test
    fun email_valido_retornaTrue() {
        assertTrue(validators.isValidEmail("test@test.com"))
    }

    @Test
    fun email_invalido_retornaFalse() {
        assertFalse(validators.isValidEmail("correo_invalido"))
    }

    // ---------------------------------------------------
    // NOMBRE
    // ---------------------------------------------------

    @Test
    fun nombre_valido_retornaTrue() {
        assertTrue(validators.isValidName("Juan Perez"))
    }

    @Test
    fun nombre_conNumeros_retornaFalse() {
        assertFalse(validators.isValidName("Juan123"))
    }

    // ---------------------------------------------------
    // TELEFONO
    // ---------------------------------------------------

    @Test
    fun telefono_chileno_valido_retornaTrue() {
        assertTrue(validators.isValidChileanPhone("912345678"))
    }

    @Test
    fun telefono_chileno_invalido_retornaFalse() {
        assertFalse(validators.isValidChileanPhone("81234567"))
    }

    // ---------------------------------------------------
    // PASSWORD
    // ---------------------------------------------------

    @Test
    fun password_segura_retornaTrue() {
        assertTrue(validators.isValidPassword("1234A."))
    }

    @Test
    fun password_insegura_retornaFalse() {
        assertFalse(validators.isValidPassword("1234"))
    }

    @Test
    fun password_errorMessage_noEsNulo() {
        val error = validators.getPasswordErrorMessage("1234")
        assertNotNull(error)
        assertTrue(error!!.isNotBlank())
    }
}
