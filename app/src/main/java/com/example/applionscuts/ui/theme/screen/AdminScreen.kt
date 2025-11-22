// Archivo: com/example/applionscuts/ui/screen/AdminScreen.kt
package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.model.Haircut
import com.example.applionscuts.ui.theme.viewmodel.BarberViewModel
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.BookingViewModel
import com.example.applionscuts.viewmodel.HaircutViewModel

// -----------------------------------------------------
// ADMIN SCREEN ROOT
// -----------------------------------------------------
@Composable
fun AdminScreen(
    productViewModel: ProductViewModel,
    bookingViewModel: BookingViewModel,
    barberViewModel: BarberViewModel,
    haircutViewModel: HaircutViewModel,
    onBack: () -> Unit
) {
    var screen by remember { mutableStateOf("main") }

    when (screen) {
        "main" -> AdminMainScreen(
            productViewModel = productViewModel,
            bookingViewModel = bookingViewModel,
            onGoToBarbers = { screen = "barbers" },
            onGoToServices = { screen = "services" },
            onBack = onBack
        )

        "barbers" -> BarberAdminScreen(
            barberViewModel = barberViewModel,
            onBack = { screen = "main" }
        )

        "services" -> ServiceAdminScreen(
            haircutViewModel = haircutViewModel,
            onBack = { screen = "main" }
        )
    }
}

// -----------------------------------------------------
// MAIN ADMIN PANEL
// -----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    productViewModel: ProductViewModel,
    bookingViewModel: BookingViewModel,
    onGoToBarbers: () -> Unit,
    onGoToServices: () -> Unit,
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
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {

            // Gestión de barberos y servicios
            item {
                Button(
                    onClick = onGoToBarbers,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Gestionar Barberos") }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onGoToServices,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Gestionar Servicios") }

                Spacer(Modifier.height(24.dp))
            }

            // AGREGAR PRODUCTO
            item {
                Text("Agregar Producto", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = brand, onValueChange = { brand = it },
                    label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it },
                    label = { Text("Descripción corta") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = longDescription, onValueChange = { longDescription = it },
                    label = { Text("Descripción larga") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = stock, onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && price.isNotBlank()) {
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

                            name = ""
                            brand = "Lions Basics"
                            price = ""
                            description = ""
                            longDescription = ""
                            stock = "30"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Agregar Producto") }

                Spacer(Modifier.height(24.dp))
            }

            // LISTA DE PRODUCTOS
            item {
                Text("Productos", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
            }

            items(products) { product ->
                ProductAdminItem(
                    product = product,
                    onDelete = { productViewModel.deleteProductFromDatabase(product) }
                )
                Spacer(Modifier.height(8.dp))
            }

            // CITAS
            item {
                Spacer(Modifier.height(24.dp))
                Text("Citas Agendadas", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                if (appointments.isEmpty()) {
                    Text("No hay citas agendadas.")
                }
            }

            items(appointments) { appointment ->
                AppointmentAdminItem(appointment)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// -----------------------------------------------------
// COMPONENTES
// -----------------------------------------------------
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
                Text("Marca: ${product.brand}")
                Text("$${product.price}")
                Text("Stock: ${product.stock}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AppointmentAdminItem(appointment: AppointmentEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Barbero: ${appointment.barberName}")
            Text("Servicio: ${appointment.service}")
            Text("Fecha: ${appointment.date}")
            Text("Hora: ${appointment.time}")
        }
    }
}
