package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.R
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.model.Barber

class BookingViewModel : ViewModel() {

    // ---- Datos del usuario actual ----
    private var userId: String = ""
    private var userName: String = ""

    fun setUserData(id: String, name: String) {
        userId = id
        userName = name
    }

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _availableDates = MutableLiveData<List<String>>()
    val availableDates: LiveData<List<String>> = _availableDates

    private val _availableTimes = MutableLiveData<List<String>>()
    val availableTimes: LiveData<List<String>> = _availableTimes

    private val _selectedBarber = MutableLiveData<Barber?>(null)
    val selectedBarber: LiveData<Barber?> = _selectedBarber

    private val _selectedDate = MutableLiveData<String?>(null)
    val selectedDate: LiveData<String?> = _selectedDate

    private val _selectedTime = MutableLiveData<String?>(null)
    val selectedTime: LiveData<String?> = _selectedTime

    // ---- Citas ----
    private val _appointments = MutableLiveData<List<AppointmentEntity>>()
    val appointments: LiveData<List<AppointmentEntity>> = _appointments

    private val _bookingSuccess = MutableLiveData<Boolean>(false)
    val bookingSuccess: LiveData<Boolean> = _bookingSuccess

    init {
        loadBookingData()
    }

    private fun loadBookingData() {
        _barbers.value = listOf(
            Barber("b1", "Juan Pérez", "Especialista en Fades", R.drawable.leon),
            Barber("b2", "Pedro Gómez", "Experto en Clásicos", R.drawable.leon),
            Barber("b3", "Luis Martínez", "Maestro de Barbas", R.drawable.leon)
        )

        _availableDates.value = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")

        _availableTimes.value = (9..21).map { String.format("%02d:00", it) }

        // ✅ EJEMPLO CORREGIDO
        _appointments.value = listOf(
            AppointmentEntity(
                id = 1,
                userId = 123,
                userName = "Carlos Soto",
                barberName = "Juan Pérez",
                service = "Mid Fade",
                date = "2025-10-20",
                time = "10:00"
            ),
            AppointmentEntity(
                id = 2,
                userId = 124,
                userName = "Marco Díaz",
                barberName = "Pedro Gómez",
                service = "Corte Clásico",
                date = "2025-10-21",
                time = "15:00"
            )
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

        val userIdInt = userId.toIntOrNull() ?: 0

        val newAppointment = AppointmentEntity(
            userId = userIdInt,
            userName = userName,
            barberName = barber.name,
            service = "Corte de Cabello",
            date = date,
            time = time
        )

        val updatedList = (_appointments.value ?: emptyList()) + newAppointment
        _appointments.value = updatedList

        _bookingSuccess.value = true
    }
}
