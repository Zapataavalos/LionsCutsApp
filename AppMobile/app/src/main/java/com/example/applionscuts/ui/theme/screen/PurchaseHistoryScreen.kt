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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.applionscuts.data.local.purchase.PurchaseEntity
import com.example.applionscuts.viewmodel.PurchaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryScreen(
    purchaseViewModel: PurchaseViewModel,
    currentUserId: Int,
    onBack: () -> Unit
) {
    val purchases by purchaseViewModel.purchaseHistory.observeAsState(emptyList())

    // Cargar solo compras del usuario
    LaunchedEffect(currentUserId) {
        purchaseViewModel.loadPurchasesByUserId(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Compras") },
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
                .padding(padding)
                .padding(16.dp)
        ) {

            if (purchases.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has realizado compras.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(purchases) { purchase ->
                PurchaseHistoryItem(purchase)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PurchaseHistoryItem(purchase: PurchaseEntity) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text("Compra #${purchase.id}", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))
            Text("Total: $${purchase.totalAmount.toInt()}")

            Spacer(Modifier.height(6.dp))
            Text("Método: ${purchase.deliveryMethod}")

            if (purchase.deliveryMethod == "Envío a Domicilio") {
                Text("Dirección: ${purchase.address}")
            }

            Spacer(Modifier.height(6.dp))
            Text("Carrito:", fontWeight = FontWeight.Bold)

            Text(purchase.cartJson) // Más adelante lo haremos bonito 😎

        }
    }
}
