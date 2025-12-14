package com.example.applionscuts.data.repository

import com.example.applionscuts.data.client.usuarios.AuthApi
import com.example.applionscuts.data.client.usuarios.UsuariosApi
import com.example.applionscuts.data.local.user.UserDao
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class UserRepositoryTest {

    private lateinit var repository: UserRepository

    private val authApi: AuthApi = mock()
    private val usuariosApi: UsuariosApi = mock()
    private val userDao: UserDao = mock()

    @Before
    fun setUp() {
        repository = UserRepository(
            authApi = authApi,
            usuariosApi = usuariosApi,
            userDao = userDao
        )
    }

    @Test
    fun usuarioRepository_instanciaCorrecta() {
        assertNotNull(repository)
    }
}
