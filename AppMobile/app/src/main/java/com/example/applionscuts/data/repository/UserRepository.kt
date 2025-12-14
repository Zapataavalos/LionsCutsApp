package com.example.applionscuts.data.repository

import android.util.Log
import com.example.applionscuts.data.client.usuarios.AuthApi
import com.example.applionscuts.data.client.usuarios.UsuariosApi
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import com.example.applionscuts.data.local.user.UserDao

class UserRepository(
    private val userDao: UserDao,
    private val usuariosApi: UsuariosApi,
    private val authApi: AuthApi
) {

    // -----------------------------
    // LOGIN
    // -----------------------------
    suspend fun login(email: String, password: String): Result<UsuarioDto> {
        return try {
            val request = UsuarioDto(
                email = email,
                password = password
            )

            val response = authApi.login(request)

            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    // -----------------------------
    // REGISTRO
    // -----------------------------
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<UsuarioDto> {

        return try {
            // Verificar si email existe
            try {
                val existing = usuariosApi.getUsuarioByEmail(email)
                if (existing != null) {
                    return Result.failure(Exception("El usuario ya existe"))
                }
            } catch (e: Exception) {
                Log.i("UserRepository", "Email no registrado, se puede crear")
            }

            val request = UsuarioDto(
                nombre = name,
                email = email,
                password = password,
                telefono = phone,
                username = email
            )

            val response = authApi.register(request)

            if (response.isSuccessful) {
                val newUser = response.body()
                if (newUser != null) {
                    Result.success(newUser)
                } else {
                    Result.failure(Exception("No se pudo crear el usuario"))
                }
            } else {
                Result.failure(Exception("Error al registrar usuario"))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    // -----------------------------
    // RECUPERAR CONTRASEÑA
    // -----------------------------
    suspend fun emailExists(email: String): Boolean {
        return try {
            usuariosApi.getUsuarioByEmail(email) != null
        } catch (e: Exception) {
            false
        }
    }
}
