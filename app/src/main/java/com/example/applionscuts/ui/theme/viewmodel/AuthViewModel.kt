package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import com.example.applionscuts.domain.validation.Validators
import com.example.applionscuts.data.repository.UserRepository
import com.example.applionscuts.model.User
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val validators = Validators()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentUserName = MutableLiveData<String?>()
    val currentUserName: LiveData<String?> = _currentUserName

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    private val _passwordResetMessage = MutableLiveData<String?>()
    val passwordResetMessage: LiveData<String?> = _passwordResetMessage


    // LOGIN
    fun login(email: String, password: String) {
        _errorMessage.value = null

        // LOGIN ADMIN MANUAL
        if (email == "Admin@admin.cl" && password == "1234A.") {

            val adminUser = User(
                id = 0,
                name = "Administrador",
                email = email,
                phone = "000000000",
                password = password,
                role = "admin",
                barberSpecificData = null
            )

            _currentUser.value = adminUser
            _currentUserName.value = adminUser.name
            _isLoggedIn.value = true
            _isAdmin.value = true
            return
        }

        // Validaciones
        if (email.isBlank()) {
            _errorMessage.value = "El campo email no puede estar vacío"
            return
        }
        if (!validators.isValidEmail(email)) {
            _errorMessage.value = "Email no válido"
            return
        }
        if (password.isBlank()) {
            _errorMessage.value = "Ingresa tu contraseña"
            return
        }

        // LOGIN NORMAL BD LOCAL
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            result.fold(
                onSuccess = { usuarioDto ->
                    val loggedUser = User(
                        id = usuarioDto.id,
                        name = usuarioDto.nombre,
                        email = usuarioDto.email,
                        phone = usuarioDto.telefono,
                        password = usuarioDto.password,
                        role = "cliente"
                    )
                    _currentUser.postValue(loggedUser)
                    _currentUserName.postValue(loggedUser?.name ?: "Usuario")
                    _isLoggedIn.postValue(true)
                    _isAdmin.postValue(false )
                    _errorMessage.postValue(null)
                },
                onFailure = { e ->
                    _isLoggedIn.postValue(false)
                    _isAdmin.postValue(false)
                    _errorMessage.postValue("Credenciales incorrectas")
                }
            )

        }
    }


    // Recuperar contraseña

    fun recoverPassword(email: String) {
        _passwordResetMessage.value = null

        if (email.isBlank()) {
            _passwordResetMessage.value = "Debes ingresar un email"
            return
        }

        if (!validators.isValidEmail(email)) {
            _passwordResetMessage.value = "El email no es válido"
            return
        }

        viewModelScope.launch {

            val exists = userRepository.emailExists(email)

            if (exists) {
                // Aquí luego llamarás a tu microservicio real
                _passwordResetMessage.value =
                    "Se envió un enlace de recuperación a tu correo"
            } else {
                _passwordResetMessage.value =
                    "Si el correo está registrado, recibirás un enlace de recuperación"
            }
        }
    }


    // REGISTRO

    fun register(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        _errorMessage.value = null

        when {
            !validators.isValidName(name) ->
                _errorMessage.value = "El nombre solo puede contener letras y espacios"

            !validators.isValidEmail(email) ->
                _errorMessage.value = "El formato del email no es válido"

            !validators.isValidChileanPhone(phone) ->
                _errorMessage.value = "El teléfono debe tener 9 dígitos y comenzar con 9 (912345678)"

            !validators.isValidPassword(password) ->
                _errorMessage.value = validators.getPasswordErrorMessage(password)

            password != confirmPassword ->
                _errorMessage.value = "Las contraseñas no coinciden"

            else -> {
                viewModelScope.launch {
                    val result = userRepository.register(name, email, phone, password)
                     result.fold(
                        onSuccess = { usuarioDto ->
                            val newUser = User(
                                id = usuarioDto.id,
                                name = usuarioDto.nombre,
                                email = usuarioDto.email,
                                phone = usuarioDto.telefono,
                                password = usuarioDto.password,
                                role = "cliente"
                            )
                            _currentUser.postValue(newUser)
                            _currentUserName.postValue(email)
                            _registrationSuccess.postValue(true)
                        },
                        onFailure = { e ->
                            _registrationSuccess.postValue(false)
                            _errorMessage.postValue(
                                result.exceptionOrNull()?.message ?: "Error al registrar usuario"
                            )
                        }
                    )

//                    if (result.isSuccess) {
//                        val newUser = UsuarioDto(
//                            id = 0,
//                            name = name,
//                            email = email,
//                            phone = phone,
//                            password = password,
//                            role = "cliente"
//                        )
//
//                        _currentUser.postValue(newUser)
//                        _currentUserName.postValue(name)
//                        _registrationSuccess.postValue(true)
//
//                    } else {
//                        _registrationSuccess.postValue(false)
//                        _errorMessage.postValue(
//                            result.exceptionOrNull()?.message ?: "Error al registrar usuario"
//                        )
//                    }
                }
            }
        }
    }




    // ------------------------------------------------------------
    // Actualizar datos del usuario, AppDrawer, ProfileScreen, toast de bienvenida, etc...
    fun updateCurrentUser(name: String? = null, phone: String? = null) {
        val current = _currentUser.value ?: return

        val updatedUser = current.copy(
            name = name ?: current.name,
            phone = phone ?: current.phone
        )

        _currentUser.value = updatedUser
        _currentUserName.value = updatedUser.name
    }

    fun updateCurrentUserName(newName: String) {
        _currentUserName.value = newName
        _currentUser.value = _currentUser.value?.copy(name = newName)
    }


    // Cerrar sesion

    fun logout() {
        _isLoggedIn.value = false
        _currentUserName.value = ""
        _currentUser.value = null
        _isAdmin.value = false
    }
}
