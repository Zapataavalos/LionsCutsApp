package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.domain.validation.Validators
import com.example.applionscuts.data.repository.UserRepository
import com.example.applionscuts.data.local.user.User
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

    private val _currentUserName = MutableLiveData<String>()
    val currentUserName: LiveData<String> = _currentUserName

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin


    // ------------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------------
    fun login(email: String, password: String) {
        _errorMessage.value = null

        // --- LOGIN ADMIN SIN BASE DE DATOS ---
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

        // LOGIN NORMAL
        viewModelScope.launch {
            val result = userRepository.login(email, password)

            if (result.isSuccess) {
                val user = result.getOrNull()

                _currentUser.postValue(user)
                _currentUserName.postValue(user?.name ?: "Usuario")
                _isLoggedIn.postValue(true)
                _isAdmin.postValue(user?.role == "admin")
                _errorMessage.postValue(null)

            } else {
                _isLoggedIn.postValue(false)
                _isAdmin.postValue(false)
                _errorMessage.postValue("Credenciales incorrectas")
            }
        }
    }


    // ------------------------------------------------------------
    // REGISTRO
    // ------------------------------------------------------------
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

                    if (result.isSuccess) {

                        val newUser = User(
                            id = 0,
                            name = name,
                            email = email,
                            phone = phone,
                            password = password,
                            role = "cliente"
                        )

                        _currentUser.postValue(newUser)
                        _currentUserName.postValue(name)
                        _registrationSuccess.postValue(true)

                    } else {
                        _registrationSuccess.postValue(false)
                        _errorMessage.postValue(
                            result.exceptionOrNull()?.message ?: "Error al registrar usuario"
                        )
                    }
                }
            }
        }
    }


    // ------------------------------------------------------------
    // 🔥 ACTUALIZAR SOLO EL NOMBRE GLOBAL
    // ------------------------------------------------------------
    fun updateCurrentUserName(newName: String) {
        _currentUserName.value = newName
        _currentUser.value = _currentUser.value?.copy(name = newName)
    }


    // ------------------------------------------------------------
    // 🔥 ACTUALIZAR NOMBRE Y TELÉFONO (SINCRONIZACIÓN GLOBAL)
    // ------------------------------------------------------------
    fun updateCurrentUser(name: String? = null, phone: String? = null) {
        val current = _currentUser.value ?: return

        val updatedUser = current.copy(
            name = name ?: current.name,
            phone = phone ?: current.phone
        )

        _currentUser.value = updatedUser
        _currentUserName.value = updatedUser.name
    }


    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------
    fun logout() {
        _isLoggedIn.value = false
        _currentUserName.value = ""
        _currentUser.value = null
        _isAdmin.value = false
    }
}
