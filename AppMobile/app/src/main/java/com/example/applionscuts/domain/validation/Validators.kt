package com.example.applionscuts.domain.validation

import android.util.Patterns

class Validators {

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in "!@#$%^&*(),.?\":{}|<>" }
    }

    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            !password.any { it.isUpperCase() } -> "Debe contener al menos una letra mayúscula"
            !password.any { it.isDigit() } -> "Debe contener al menos un número"
            !password.any { it in "!@#$%^&*(),.?\":{}|<>" } -> "Debe contener al menos un carácter especial (ej: !, @, #, $, etc.)"
            else -> null
        }
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.all { it.isLetter() || it.isWhitespace() }
    }


    fun isValidChileanPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }

        val cleanedNumber = when {
            digits.startsWith("56") && digits.length > 2 -> digits.substring(2)  // elimina 56
            digits.startsWith("56") -> ""  // evita crash si es incompleto
            else -> digits
        }

        return cleanedNumber.length == 9 && cleanedNumber.startsWith("9")
    }
}
