package com.example.applionscuts.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import com.example.applionscuts.data.repository.BarberRepository
import com.example.applionscuts.model.Barber
import kotlinx.coroutines.flow.StateFlow

class BarberViewModel : ViewModel() {

    private val repository = BarberRepository()

    val barbers: StateFlow<List<Barber>> = repository.barbers

    fun addBarber(name: String, description: String) {
        val barber = Barber(
            id = "b" + System.currentTimeMillis(),
            name = name,
            description = description
        )
        repository.addBarber(barber)
    }

    fun deleteBarber(id: String) {
        repository.deleteBarber(id)
    }
}

