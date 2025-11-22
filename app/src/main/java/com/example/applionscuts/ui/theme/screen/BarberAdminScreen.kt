package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.applionscuts.model.Barber
import com.example.applionscuts.ui.theme.viewmodel.BarberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberAdminScreen(
    barberViewModel: BarberViewModel,
    onBack: () -> Unit
) {
    val barbers by barberViewModel.barbers.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Barberos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // --- FORMULARIO ---
            item {
                Text("Agregar Barbero", style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && description.isNotBlank()) {
                            barberViewModel.addBarber(name, description)
                            name = ""
                            description = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar")
                }

                Spacer(Modifier.height(24.dp))
            }

            // --- LISTA ---
            items(barbers) { barber ->
                BarberAdminItem(
                    barber = barber,
                    onDelete = { barberViewModel.deleteBarber(barber.id) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BarberAdminItem(
    barber: Barber,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(barber.name, style = MaterialTheme.typography.titleMedium)
            Text(barber.description)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
