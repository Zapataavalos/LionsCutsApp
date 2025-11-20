// Archivo: com/example/applionscuts/viewmodel/BookingViewModel.kt
package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.R
import com.example.applionscuts.model.Appointment
import com.example.applionscuts.model.Barber

class BookingViewModel : ViewModel() {

    // Listas de Datos
    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _availableDates = MutableLiveData<List<String>>()
    val availableDates: LiveData<List<String>> = _availableDates

    private val _availableTimes = MutableLiveData<List<String>>()
    val availableTimes: LiveData<List<String>> = _availableTimes

    // Estado de Selección del Usuario
    private val _selectedBarber = MutableLiveData<Barber?>(null)
    val selectedBarber: LiveData<Barber?> = _selectedBarber

    private val _selectedDate = MutableLiveData<String?>(null)
    val selectedDate: LiveData<String?> = _selectedDate

    private val _selectedTime = MutableLiveData<String?>(null)
    val selectedTime: LiveData<String?> = _selectedTime

    // --- AÑADIDO: Lista de citas agendadas ---
    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> = _appointments

    // Estado de la Reserva
    private val _bookingSuccess = MutableLiveData<Boolean>(false)
    val bookingSuccess: LiveData<Boolean> = _bookingSuccess

    init {
        loadBookingData()
    }

    private fun loadBookingData() {
        // Datos Simulados
        _barbers.value = listOf(
            Barber("b1", "Juan Pérez", "Especialista en Fades", R.drawable.leon),
            Barber("b2", "Pedro Gómez", "Experto en Clásicos", R.drawable.leon),
            Barber("b3", "Luis Martínez", "Maestro de Barbas", R.drawable.leon)
        )

        _availableDates.value = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")

        _availableTimes.value = (9..21).map { String.format("%02d:00", it) }

        // --- AÑADIDO: Citas simuladas para el administrador ---
        _appointments.value = listOf(
            Appointment("1", "Juan Pérez", "Mid Fade", "2025-10-20", "10:00"),
            Appointment("2", "Pedro Gómez", "Corte Clásico", "2025-10-21", "15:00"),
            Appointment("3", "Luis Martínez", "Buzz Cut", "2025-10-22", "12:00")
        )
    }

    fun onBarberSelected(barber: Barber) {
        _selectedBarber.value = barber
    }

    fun onDateSelected(date: String) {
        _selectedDate.value = date
    }

    fun onTimeSelected(time: String) {
        _selectedTime.value = time
    }

    fun confirmBooking() {
        val barber = _selectedBarber.value ?: return
        val date = _selectedDate.value ?: return
        val time = _selectedTime.value ?: return

        // --- AÑADIDO: Guardar la cita en la lista ---
        val newAppointment = Appointment(
            id = (_appointments.value?.size ?: 0).toString(),
            barberName = barber.name,
            service = "Corte de Cabello", // Puedes mejorar esto en el futuro
            date = date,
            time = time
        )

        val updatedList = (_appointments.value ?: emptyList()) + newAppointment
        _appointments.value = updatedList

        println("CITA CONFIRMADA:")
        println("Barbero: ${barber.name}")
        println("Fecha: $date")
        println("Hora: $time")

        _bookingSuccess.value = true
    }
}