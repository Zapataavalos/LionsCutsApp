package com.example.applionscuts.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import com.example.applionscuts.data.repository.UserRepository
import com.example.applionscuts.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

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
    // VALIDACIONES LOGIN
    // ---------------------------------------------------

    @Test
    fun login_emailVacio_muestraError() {
        viewModel.login("", "1234")

        assertEquals(
            "El campo email no puede estar vacío",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun login_emailInvalido_muestraError() {
        viewModel.login("correo_invalido", "1234")

        assertEquals(
            "Email no válido",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun login_passwordVacio_muestraError() {
        viewModel.login("test@test.com", "")

        assertEquals(
            "Ingresa tu contraseña",
            viewModel.errorMessage.value
        )
    }

    // ---------------------------------------------------
    // REGISTRO
    // ---------------------------------------------------

    @Test
    fun register_exitoso_actualizaEstado() = runTest {
        val usuarioDto = UsuarioDto(
            id = 1,
            nombre = "Juan",
            email = "juan@test.com",
            telefono = "912345678",
            password = "1234A."
        )

        whenever(
            userRepository.register(
                "Juan",
                "juan@test.com",
                "912345678",
                "1234A."
            )
        ).thenReturn(Result.success(usuarioDto))

        viewModel.register(
            name = "Juan",
            email = "juan@test.com",
            phone = "912345678",
            password = "1234A.",
            confirmPassword = "1234A."
        )

        advanceUntilIdle()

        assertTrue(viewModel.registrationSuccess.value == true)
        assertEquals("juan@test.com", viewModel.currentUserName.value)
        assertNotNull(viewModel.currentUser.value)
    }

    @Test
    fun register_passwordsNoCoinciden_muestraError() {
        viewModel.register(
            name = "Juan",
            email = "juan@test.com",
            phone = "912345678",
            password = "1234A.",
            confirmPassword = "4321.B"
        )

        assertEquals(
            "Las contraseñas no coinciden",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun register_nombreInvalido_muestraError() {
        viewModel.register(
            name = "Juan123",
            email = "juan@test.com",
            phone = "912345678",
            password = "1234A.",
            confirmPassword = "1234A."
        )

        assertEquals(
            "El nombre solo puede contener letras y espacios",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun register_telefonoInvalido_muestraError() {
        viewModel.register(
            name = "Juan",
            email = "juan@test.com",
            phone = "81234567",
            password = "1234A.",
            confirmPassword = "1234A."
        )

        assertEquals(
            "El teléfono debe tener 9 dígitos y comenzar con 9 (912345678)",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun register_emailInvalido_muestraError() {
        viewModel.register(
            name = "Juan",
            email = "correo_invalido",
            phone = "912345678",
            password = "1234A.",
            confirmPassword = "1234A."
        )

        assertEquals(
            "El formato del email no es válido",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun register_passwordDebil_muestraError() {
        viewModel.register(
            name = "Juan",
            email = "juan@test.com",
            phone = "912345678",
            password = "1234",
            confirmPassword = "1234"
        )

        assertNotNull(viewModel.errorMessage.value)
        assertTrue(viewModel.errorMessage.value!!.isNotBlank())
    }

    // ---------------------------------------------------
    // RECUPERAR CONTRASEÑA
    // ---------------------------------------------------

    @Test
    fun recoverPassword_emailVacio_muestraMensaje() {
        viewModel.recoverPassword("")

        assertEquals(
            "Debes ingresar un email",
            viewModel.passwordResetMessage.value
        )
    }

    @Test
    fun recoverPassword_emailInvalido_muestraMensaje() {
        viewModel.recoverPassword("correo_invalido")

        assertEquals(
            "El email no es válido",
            viewModel.passwordResetMessage.value
        )
    }

    @Test
    fun recoverPassword_emailExiste_muestraMensajeExito() = runTest {
        whenever(userRepository.emailExists("test@test.com"))
            .thenReturn(true)

        viewModel.recoverPassword("test@test.com")

        advanceUntilIdle()

        assertEquals(
            "Se envió un enlace de recuperación a tu correo",
            viewModel.passwordResetMessage.value
        )
    }

    //  TEST: email NO existe

    @Test
    fun recoverPassword_emailNoExiste_muestraMensajeGenerico() = runTest {
        whenever(userRepository.emailExists("noexiste@test.com"))
            .thenReturn(false)

        viewModel.recoverPassword("noexiste@test.com")

        advanceUntilIdle()

        assertEquals(
            "Si el correo está registrado, recibirás un enlace de recuperación",
            viewModel.passwordResetMessage.value
        )
    }
}
