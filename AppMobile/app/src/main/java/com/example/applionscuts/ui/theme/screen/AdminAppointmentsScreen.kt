package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppointmentsScreen(
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit
) {
    val appointments by bookingViewModel.appointments.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas Agendadas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            Modifier.padding(padding).padding(16.dp)
        ) {
            if (appointments.isEmpty()) {
                item { Text("No hay citas registradas.") }
            } else {
                items(appointments) { appointment ->
                    AdminAppointmentItem(appointment)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun AdminAppointmentItem(appointment: AppointmentEntity) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Cliente: ${appointment.userName}")
            Text("Barbero: ${appointment.barberName}")
            Text("Servicio: ${appointment.service}")
            Text("Fecha: ${appointment.date}")
            Text("Hora: ${appointment.time}")
        }
    }
}
