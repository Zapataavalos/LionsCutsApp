package com.example.applionscuts.data.repository

import com.example.applionscuts.model.Barber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BarberRepository {

    private val barberList = mutableListOf(
        Barber("b1", "Juan Pérez", "Especialista en Fades"),
        Barber("b2", "Pedro Gómez", "Experto en clásicos"),
        Barber("b3", "Luis Martínez", "Maestro de barbas")
    )

    private val _barbers = MutableStateFlow<List<Barber>>(barberList)
    val barbers = _barbers.asStateFlow()

    fun addBarber(barber: Barber) {
        barberList.add(barber)
        _barbers.value = barberList.toList()
    }

    fun deleteBarber(id: String) {
        barberList.removeAll { it.id == id }
        _barbers.value = barberList.toList()
    }
}
