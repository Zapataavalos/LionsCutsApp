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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(
    productViewModel: ProductViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Lions Basics") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var longDescription by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("30") }

    val products by productViewModel.products.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Productos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            item {
                Text("Agregar Producto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = brand, onValueChange = { brand = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción corta") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = longDescription, onValueChange = { longDescription = it },
                    label = { Text("Descripción larga") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

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
                ) {
                    Text("Agregar Producto")
                }

                Spacer(Modifier.height(24.dp))
            }

            items(products) { product ->
                AdminProductItem(
                    product = product,
                    onDelete = { productViewModel.deleteProductFromDatabase(product) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AdminProductItem(
    product: Product,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("Marca: ${product.brand}")
                Text("Precio: $${product.price}")
                Text("Stock: ${product.stock}")
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
