package com.example.applionscuts.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.applionscuts.model.Haircut
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.HaircutViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaircutsScreen(
    viewModel: HaircutViewModel,
    productViewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val haircuts by viewModel.haircuts.observeAsState(emptyList())
    val selectedHaircut by viewModel.selectedHaircut.observeAsState(null)

    selectedHaircut?.let { haircut ->
        HaircutDetailDialog(
            haircut = haircut,
            onDismiss = { viewModel.onDialogDismiss() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestros Servicios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { productViewModel.onShowCart() }) {
                        Icon(Icons.Default.ShoppingCart, "Carrito")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(haircuts) { haircut ->
                HaircutItem(
                    haircut = haircut,
                    onViewMoreClicked = { viewModel.onHaircutSelected(haircut) }
                )
            }
        }
    }
}

@Composable
fun HaircutItem(haircut: Haircut, onViewMoreClicked: () -> Unit) {
    //  formateador a moneda chilena
    val chileLocale = Locale("es", "CL")
    val currencyFormat = NumberFormat.getCurrencyInstance(chileLocale)
    currencyFormat.maximumFractionDigits = 0 // es para no mostrar ,00

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column {
            Image(painter = painterResource(id = haircut.imageResId), contentDescription = haircut.name, modifier = Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = haircut.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text(
                    text = currencyFormat.format(haircut.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                // --- FIN ---
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = haircut.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onViewMoreClicked) { Text("Ver más") }
            }
        }
    }
}
@Composable
fun HaircutDetailDialog(haircut: Haircut, onDismiss: () -> Unit) {
    val chileLocale = Locale("es", "CL")
    val currencyFormat = NumberFormat.getCurrencyInstance(chileLocale)
    currencyFormat.maximumFractionDigits = 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(haircut.name, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Image(painter = painterResource(id = haircut.imageResId), contentDescription = haircut.name, modifier = Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(16.dp))
                Text(haircut.longDescription, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currencyFormat.format(haircut.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Cerrar") } }
    )
}