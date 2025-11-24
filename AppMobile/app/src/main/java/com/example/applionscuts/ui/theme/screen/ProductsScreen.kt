package com.example.applionscuts.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    productViewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val productList by productViewModel.products.observeAsState(emptyList())
    val selectedProduct by productViewModel.selectedProduct.observeAsState(null)

    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            viewModel = productViewModel,
            onDismiss = { productViewModel.onDialogDismiss() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { productViewModel.onShowCart() }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(productList) { product ->
                ProductItem(
                    product = product,
                    onViewMore = { productViewModel.onProductSelected(product) }
                )
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onViewMore: () -> Unit) {
    val chileLocale = Locale("es", "CL")
    val currencyFormat = NumberFormat.getCurrencyInstance(chileLocale)
    currencyFormat.maximumFractionDigits = 0

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = product.name, fontWeight = FontWeight.Bold)
            Text(text = product.brand, style = MaterialTheme.typography.bodySmall)
            Text(
                text = currencyFormat.format(product.price),
                color = MaterialTheme.colorScheme.primary
            )

            val stockText = if (product.stock > 0) {
                "Stock: ${product.stock}"
            } else {
                "Sin stock"
            }

            Text(
                text = stockText,
                style = MaterialTheme.typography.bodySmall,
                color = if (product.stock > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onViewMore,
                modifier = Modifier.align(Alignment.End),
                enabled = product.stock > 0
            ) {
                Text(if (product.stock > 0) "Ver más" else "Agotado")
            }
        }
    }
}
