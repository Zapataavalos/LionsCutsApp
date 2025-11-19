package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.domain.validation.Validators
import com.example.applionscuts.model.UserRole
import com.example.applionscuts.data.repository.UserRepository
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

    fun login(email: String, password: String) {
        _errorMessage.value = null
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
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if (result.isSuccess) {
                _isLoggedIn.postValue(true)
                val user = result.getOrNull()
                _currentUserName.postValue(user?.name ?: "Usuario")
                _errorMessage.postValue(null)
            } else {
                _isLoggedIn.postValue(false)
                _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Error de inicio de sesión")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        _errorMessage.value = null
        when {
            !validators.isValidName(name) -> {
                _errorMessage.value = "El nombre solo puede contener letras y espacios"
                return
            }
            !validators.isValidEmail(email) -> {
                _errorMessage.value = "El formato del email no es válido"
                return
            }
            !validators.isValidChileanPhone(phone) -> {
                _errorMessage.value = "El teléfono debe tener 9 dígitos y comenzar con 9 (ej: 912345678)"
                return
            }
            !validators.isValidPassword(password) -> {
                _errorMessage.value = validators.getPasswordErrorMessage(password)
                return
            }
            password != confirmPassword -> {
                _errorMessage.value = "Las contraseñas no coinciden"
                return
            }
        }
        viewModelScope.launch {
            val result = userRepository.register(name, email, phone, password)
            if (result.isSuccess) {
                _registrationSuccess.postValue(true)
                _currentUserName.postValue(name)
                _errorMessage.postValue(null)
            } else {
                _registrationSuccess.postValue(false)
                _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Error al registrar usuario")
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUserName.value = ""
    }
}