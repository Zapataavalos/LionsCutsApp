package com.example.applionscuts.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.applionscuts.model.CartItem
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDialog(viewModel: ProductViewModel) {
    val cartItems by viewModel.cartItems.observeAsState(emptyList())
    val totalPrice by viewModel.totalPrice.observeAsState(0.0)

    val chileLocale = Locale("es", "CL")
    val currencyFormat = NumberFormat.getCurrencyInstance(chileLocale)
    currencyFormat.maximumFractionDigits = 0

    Dialog(onDismissRequest = { viewModel.onHideCart() }) {
        Card(modifier = Modifier.fillMaxWidth().height(600.dp)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tu Carrito", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.onHideCart() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                if (cartItems.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Tu carrito está vacío :(",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartItems) { item ->
                            CartItemRow(item = item, viewModel = viewModel, currencyFormat = currencyFormat)
                            Divider()
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            text = currencyFormat.format(totalPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.onShowPayment() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cartItems.isNotEmpty()
                    ) {
                        Text("Proceder al Pago")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: ProductViewModel, currencyFormat: java.text.NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = item.product.imageResId),
            contentDescription = item.product.name,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text("Marca: ${item.product.brand}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${currencyFormat.format(item.product.price)} c/u",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.decreaseQuantity(item.product.id) }) {
                Icon(Icons.Default.Remove, contentDescription = "Quitar uno")
            }
            Text("${item.quantity}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { viewModel.increaseQuantity(item.product.id) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir uno")
            }
            IconButton(onClick = { viewModel.removeItem(item.product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}