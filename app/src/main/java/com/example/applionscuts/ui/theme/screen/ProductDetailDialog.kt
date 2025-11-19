package com.example.applionscuts.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductDetailDialog(
    product: Product,
    viewModel: ProductViewModel,
    onDismiss: () -> Unit
) {
    val chileLocale = Locale("es", "CL")
    val currencyFormat = NumberFormat.getCurrencyInstance(chileLocale)
    currencyFormat.maximumFractionDigits = 0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = product.name, fontWeight = FontWeight.Bold)
                Text(text = product.brand, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = currencyFormat.format(product.price),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.longDescription)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (product.stock > 0) "Stock: ${product.stock}" else "Sin stock",
                    color = if (product.stock > 0)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.addToCart(product)
                        if (product.stock > 0) onDismiss()
                    },
                    enabled = product.stock > 0
                ) {
                    Text(if (product.stock > 0) "Agregar al carrito" else "Agotado")
                }
            }
        }
    )
}
