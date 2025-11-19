package com.example.applionscuts.data.repository

import com.example.applionscuts.data.local.user.User
import com.example.applionscuts.data.local.user.UserDao

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.loginUser(email, password)
            if (user != null) Result.success(user)
            else Result.failure(Exception("Credenciales incorrectas"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<Unit> {
        return try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null)
                return Result.failure(Exception("El usuario ya existe"))
            userDao.insertUser(User(name = name, email = email, phone = phone, password = password))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}