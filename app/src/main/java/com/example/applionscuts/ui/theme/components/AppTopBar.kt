package com.example.applionscuts.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onMenuClick: (() -> Unit)? = null,
    onCartClick: () -> Unit,
    extraActions: (@Composable () -> Unit)? = null
) {

    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton) {
                //  Mostrar flecha atrás
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            } else {
                //  Mostrar menú
                IconButton(onClick = { onMenuClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú"
                    )
                }
            }
        },
        actions = {
            extraActions?.invoke()
            // Mostrar icono del carrito
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Carrito"
                )
            }
        }
    )
}
