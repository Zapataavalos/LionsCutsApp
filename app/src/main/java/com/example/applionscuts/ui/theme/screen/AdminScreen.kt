// Archivo: com/example/applionscuts/ui/screen/AdminScreen.kt
package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.model.Appointment
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    productViewModel: ProductViewModel,
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Lions Basics") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var longDescription by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("30") }

    val products by productViewModel.products.observeAsState(emptyList())
    val appointments by bookingViewModel.appointments.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Agregar Producto", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción corta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = longDescription,
                    onValueChange = { longDescription = it },
                    label = { Text("Descripción larga") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank() && price.isNotBlank() && description.isNotBlank() && stock.isNotBlank()) {
                            // --- USAR EL MÉTODO EXISTENTE EN TU VIEWMODEL ---
                            productViewModel.addSampleProducts()
                            val newProduct = Product(
                                name = name,
                                brand = brand,
                                price = price.toDouble(),
                                description = description,
                                longDescription = longDescription,
                                imageResId = com.example.applionscuts.R.drawable.leon,
                                stock = stock.toInt()
                            )
                            productViewModel.addProductToDatabase(newProduct)
                            // Limpiar campos
                            name = ""
                            brand = "Lions Basics"
                            price = ""
                            description = ""
                            longDescription = ""
                            stock = "30"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Producto")
                }
                Spacer(Modifier.height(24.dp))
            }

            // --- Sección: Productos Existentes ---
            item {
                Text("Productos", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                products.forEach { product ->
                    ProductAdminItem(
                        product = product,
                        onDelete = {
                            // --- USAR EL MÉTODO EXISTENTE ---
                            productViewModel.deleteProductFromDatabase(product)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(24.dp))
            }

            // --- Sección: Citas Agendadas ---
            item {
                Text("Citas Agendadas", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                if (appointments.isEmpty()) {
                    Text("No hay citas agendadas.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    appointments.forEach { appointment ->
                        AppointmentAdminItem(appointment = appointment)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// --- NUEVAS FUNCIONES EN ProductViewModel (DEBES AÑADIRLAS) ---
fun ProductViewModel.addProductToDatabase(product: Product) {
    viewModelScope.launch {
        repo.addProduct(product)
        loadProducts()
    }
}

fun ProductViewModel.deleteProductFromDatabase(product: Product) {
    viewModelScope.launch {
        repo.deleteProduct(product)
        loadProducts()
    }
}

@Composable
fun ProductAdminItem(product: Product, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("Marca: ${product.brand}", style = MaterialTheme.typography.bodySmall)
                Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AppointmentAdminItem(appointment: Appointment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Barbero: ${appointment.barberName}", style = MaterialTheme.typography.bodyMedium)
            Text("Servicio: ${appointment.service}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha: ${appointment.date}", style = MaterialTheme.typography.bodyMedium)
            Text("Hora: ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}