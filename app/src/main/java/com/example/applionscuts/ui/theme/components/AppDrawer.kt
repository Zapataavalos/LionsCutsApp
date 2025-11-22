package com.example.applionscuts.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.applionscuts.R

@Composable
fun AppDrawer(
    userName: String,        // ⭐ NUEVO
    isAdmin: Boolean,
    onCloseDrawer: () -> Unit,
    onNavigateToHaircuts: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet {

        // -----------------------------------
        // HEADER
        // -----------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.leon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            // Nombre de la App (fijo)
            Text(
                text = "LionsCuts",
                style = MaterialTheme.typography.headlineSmall
            )

            // Nombre dinámico del usuario
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Divider()

        // -----------------------------------
        // OPCIONES GENERALES
        // -----------------------------------
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = {
                onCloseDrawer()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.List, "Cortes") },
            label = { Text("Ver Cortes") },
            selected = false,
            onClick = {
                onNavigateToHaircuts()
                onCloseDrawer()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ShoppingCart, "Productos") },
            label = { Text("Ver Productos") },
            selected = false,
            onClick = {
                onNavigateToProducts()
                onCloseDrawer()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person, "Perfil") },
            label = { Text("Mi Perfil") },
            selected = false,
            onClick = {
                onNavigateToProfile()
                onCloseDrawer()
            }
        )

        // Administrador
        if (isAdmin) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.AdminPanelSettings, "Panel Admin") },
                label = { Text("Panel de Administrador") },
                selected = false,
                onClick = {
                    onNavigateToAdmin()
                    onCloseDrawer()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Cerrar sesión
        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión") },
            label = { Text("Cerrar Sesión") },
            selected = false,
            onClick = {
                onLogout()
                onCloseDrawer()
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

