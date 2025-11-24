package com.example.applionscuts.data.repository

import android.util.Log
import com.example.applionscuts.data.client.usuarios.AuthApi
import com.example.applionscuts.data.client.usuarios.AuthClient
import com.example.applionscuts.data.local.user.UserDao
import com.example.applionscuts.data.client.usuarios.UsuariosApi
import com.example.applionscuts.data.client.usuarios.UsuariosClient
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto


class UserRepository(
    private val userDao: UserDao,
    private val usuariosApi: UsuariosApi ,
    private val authApi: AuthApi

) {

    // LOGIN
    suspend fun login(email: String, password: String): Result<UsuarioDto> {
        return try {
            val usuarioDto = UsuarioDto(
                username = email,
                password = password
            )
            val user = authApi.login(usuarioDto)

            if (user != null) Result.success(user)
            else Result.failure(Exception("Credenciales incorrectas"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRO
    suspend fun register(name: String, email: String, phone: String, password: String): Result<UsuarioDto> {
        return try {
            try {
                val usuarioDto = usuariosApi.getUsuarioByEmail(email)
                if (usuarioDto!= null)
                    return Result.failure(Exception("El usuario ya existe"))
            }  catch (e: Exception) {
                Log.i("User", "UserRepository - register - Email no encontrado")
            }
            val user = UsuarioDto(
                id = null,
                username = email,
                nombre = name,
                password = password,
                email = email,
                telefono = phone
            )
            val newUser = authApi.register(user)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verificar si un email existe (para recuperar contraseña)
    suspend fun emailExists(email: String): Boolean {
        return usuariosApi.getUsuarioByEmail(email) != null
    }
}
