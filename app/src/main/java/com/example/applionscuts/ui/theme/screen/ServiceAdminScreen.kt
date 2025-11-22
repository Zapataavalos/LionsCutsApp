package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.applionscuts.model.Haircut
import com.example.applionscuts.viewmodel.HaircutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceAdminScreen(
    haircutViewModel: HaircutViewModel,
    onBack: () -> Unit
) {
    val services by haircutViewModel.haircuts.observeAsState(emptyList())

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var longDesc by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Servicios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {

            // ------- AGREGAR NUEVO SERVICIO -------
            item {
                Text("Agregar Servicio", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = longDesc, onValueChange = { longDesc = it },
                    label = { Text("Descripción larga") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() &&
                            description.isNotBlank() &&
                            longDesc.isNotBlank() &&
                            price.isNotBlank()
                        ) {
                            haircutViewModel.addHaircut(
                                name = name,
                                description = description,
                                longDesc = longDesc,   // <--- CORRECTO
                                price = price.toDouble(),
                                imageResId = com.example.applionscuts.R.drawable.leon
                            )

                            name = ""
                            description = ""
                            longDesc = ""
                            price = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Servicio")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ------- LISTA -------
            item {
                Text("Servicios Registrados", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(services) { service ->
                ServiceAdminItem(
                    service = service,
                    onDelete = { haircutViewModel.deleteHaircut(service.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ServiceAdminItem(service: Haircut, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(service.name, style = MaterialTheme.typography.titleMedium)
            Text(service.description)
            Text("Precio: $${service.price}")

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
