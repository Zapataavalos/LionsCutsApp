// Archivo: com/example/applionscuts/ui/screen/AdminScreen.kt
package com.example.applionscuts.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.applionscuts.ui.theme.viewmodel.BarberViewModel
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.BookingViewModel
import com.example.applionscuts.viewmodel.HaircutViewModel

// -----------------------------------------------------
// ADMIN ROOT
// -----------------------------------------------------
@Composable
fun AdminScreen(
    productViewModel: ProductViewModel,
    bookingViewModel: BookingViewModel,
    barberViewModel: BarberViewModel,
    haircutViewModel: HaircutViewModel,
    onBack: () -> Unit
) {
    var currentScreen by remember { mutableStateOf("dashboard") }

    when (currentScreen) {

        "dashboard" -> AdminDashboard(
            onBack = onBack,
            onGoToBarbers = { currentScreen = "barbers" },
            onGoToServices = { currentScreen = "services" },
            onGoToProducts = { currentScreen = "products" },
            onGoToAppointments = { currentScreen = "appointments" }
        )

        "barbers" -> BarberAdminScreen(
            barberViewModel = barberViewModel,
            onBack = { currentScreen = "dashboard" }
        )

        "services" -> ServiceAdminScreen(
            haircutViewModel = haircutViewModel,
            onBack = { currentScreen = "dashboard" }
        )

        "products" -> AdminProductScreen(
            productViewModel = productViewModel,
            onBack = { currentScreen = "dashboard" }
        )

        "appointments" -> AdminAppointmentsScreen(
            bookingViewModel = bookingViewModel,
            onBack = { currentScreen = "dashboard" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    onBack: () -> Unit,
    onGoToBarbers: () -> Unit,
    onGoToServices: () -> Unit,
    onGoToProducts: () -> Unit,
    onGoToAppointments: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AdminModuleCard(
                title = "Barberos",
                description = "Administrar barberos",
                icon = Icons.Default.People,
                color = Color(0xFF2196F3),
                onClick = onGoToBarbers
            )

            AdminModuleCard(
                title = "Servicios",
                description = "Administrar cortes y servicios",
                icon = Icons.Default.ContentCut,
                color = Color(0xFFE91E63),
                onClick = onGoToServices
            )

            AdminModuleCard(
                title = "Productos",
                description = "Administrar catálogo de productos",
                icon = Icons.Default.Inventory2,
                color = Color(0xFF4CAF50),
                onClick = onGoToProducts
            )

            AdminModuleCard(
                title = "Citas",
                description = "Ver citas agendadas",
                icon = Icons.Default.CalendarMonth,
                color = Color(0xFFFF9800),
                onClick = onGoToAppointments
            )
        }
    }
}

// -----------------------------------------------------
// TARJETAS
// -----------------------------------------------------
@Composable
fun AdminModuleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
