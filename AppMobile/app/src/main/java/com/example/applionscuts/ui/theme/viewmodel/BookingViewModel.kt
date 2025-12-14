package com.example.applionscuts.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.data.remote.dto.Cita
import com.example.applionscuts.model.Barber
import com.example.applionscuts.data.repository.CitasRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BookingViewModel : ViewModel() {

    // ---- Datos del usuario actual ----
    private var userId: String = ""
    private var userName: String = ""

    fun setUserData(id: String, name: String) {
        userId = id
        userName = name
    }

    // ---- Repository ----
    private val citasRepository = CitasRepository()

    // ---- UI DATA ----
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

    // ---- RESULTADOS ----
    private val _appointments = MutableLiveData<List<AppointmentEntity>>()
    val appointments: LiveData<List<AppointmentEntity>> = _appointments

    private val _bookingSuccess = MutableLiveData(false)
    val bookingSuccess: LiveData<Boolean> = _bookingSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadBookingData()
    }

    // ---- DATA MOCK ----
    private fun loadBookingData() {
        _barbers.value = listOf(
            Barber("b1", "Juan Pérez", "Especialista en Fades"),
            Barber("b2", "Pedro Gómez", "Experto en Clásicos"),
            Barber("b3", "Luis Martínez", "Maestro de Barbas")
        )

        _availableDates.value = listOf(
            "2025-02-10",
            "2025-02-11",
            "2025-02-12",
            "2025-02-13",
            "2025-02-14"
        )

        _availableTimes.value = (9..21).map { String.format("%02d:00", it) }
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

    // ---- CONFIRMAR RESERVA (SEGURO, NO CRASHEA) ----
    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmBooking() {
        val barber = _selectedBarber.value ?: return
        val date = _selectedDate.value ?: return
        val time = _selectedTime.value ?: return

        val userIdLong = userId.toLongOrNull() ?: run {
            _errorMessage.value = "Usuario inválido"
            return
        }

        val fechaHora = LocalDateTime.of(
            LocalDate.parse(date),
            LocalTime.parse(time)
        ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val cita = Cita(
            clienteId = userIdLong,
            productoId = 1L,
            fechaHora = fechaHora
        )

        viewModelScope.launch {
            try {
                val response = citasRepository.crearCita(cita)

                if (response.isSuccessful) {

                    val newAppointment = AppointmentEntity(
                        userId = userIdLong.toInt(),
                        userName = userName,
                        barberName = barber.name,
                        service = "Corte de Cabello",
                        date = date,
                        time = time
                    )

                    _appointments.value =
                        (_appointments.value ?: emptyList()) + newAppointment

                    _bookingSuccess.value = true
                    _errorMessage.value = null

                } else {
                    _errorMessage.value = response.errorBody()?.string()
                    _bookingSuccess.value = false
                }

            } catch (e: Exception) {
                // 🔥 ESTA LÍNEA EVITA EL CRASH DEFINITIVAMENTE
                _errorMessage.value = "No se pudo conectar con el servidor"
                _bookingSuccess.value = false
            }
        }
    }
}
