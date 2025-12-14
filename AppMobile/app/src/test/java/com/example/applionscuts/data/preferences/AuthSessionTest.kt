package com.example.applionscuts.data.preferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import com.example.applionscuts.data.repository.UserRepository
import com.example.applionscuts.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthSessionTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private val userRepository: UserRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------------------------------------------------
    // TESTS DE SESIÓN
    // ---------------------------------------------------

    @Test
    fun authViewModel_seCreaCorrectamente() {
        assertNotNull(viewModel)
    }

    @Test
    fun login_admin_manual_iniciaSesionComoAdmin() {
        viewModel.login("Admin@admin.cl", "1234A.")

        assertTrue(viewModel.isLoggedIn.value == true)
        assertTrue(viewModel.isAdmin.value == true)
        assertEquals("Administrador", viewModel.currentUserName.value)
    }

    @Test
    fun login_exitoso_actualizaEstadoDeSesion() = runTest {
        val usuarioDto = UsuarioDto(
            id = 1,
            nombre = "Juan",
            email = "juan@test.com",
            telefono = "912345678",
            password = "1234"
        )

        whenever(userRepository.login("juan@test.com", "1234"))
            .thenReturn(Result.success(usuarioDto))

        viewModel.login("juan@test.com", "1234")

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isLoggedIn.value == true)
        assertFalse(viewModel.isAdmin.value == true)
        assertEquals("Juan", viewModel.currentUserName.value)
        assertNotNull(viewModel.currentUser.value)
    }

    @Test
    fun login_fallido_noIniciaSesion() = runTest {
        whenever(userRepository.login("fail@test.com", "1234"))
            .thenReturn(Result.failure(Exception("Credenciales incorrectas")))

        viewModel.login("fail@test.com", "1234")

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoggedIn.value == true)
        assertEquals("Credenciales incorrectas", viewModel.errorMessage.value)
    }

    @Test
    fun logout_limpiaSesion() {
        viewModel.logout()

        assertFalse(viewModel.isLoggedIn.value == true)
        assertNull(viewModel.currentUser.value)
        assertEquals("", viewModel.currentUserName.value)
        assertFalse(viewModel.isAdmin.value == true)
    }
}

