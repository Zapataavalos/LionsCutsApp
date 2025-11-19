package com.example.applionscuts.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.applionscuts.data.local.user.UserDao
import com.example.applionscuts.data.repository.UserRepository
import com.example.applionscuts.viewmodel.AuthViewModel

class AuthViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repo = UserRepository(userDao)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}