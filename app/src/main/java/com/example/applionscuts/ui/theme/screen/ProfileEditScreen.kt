package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.applionscuts.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val user = viewModel.userProfile.value

    // ===================== CAMPOS =====================
    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf("+56 9 ") }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // ===================== CARGAR TELÉFONO =====================
    LaunchedEffect(user) {
        user?.phone?.let { saved ->
            if (saved.isNotBlank()) {
                val formatted = saved.chunked(1).joinToString(" ")
                phone = "+56 $formatted"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // =======================================================
            // DATOS PERSONALES
            // =======================================================
            Text("Datos Personales", style = MaterialTheme.typography.titleMedium)

            // -------- NOMBRE SOLO LETRAS --------
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.all { c -> c.isLetter() || c.isWhitespace() }) {
                        name = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") }
            )

            // -------- GUARDAR NOMBRE --------
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "El nombre no puede estar vacío"
                    } else {
                        viewModel.updateUserName(name)
                        successMessage = "Nombre actualizado correctamente"
                        errorMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Nombre")
            }

            // =======================================================
            // TELÉFONO
            // =======================================================

            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    if (!newValue.startsWith("+56 9")) return@OutlinedTextField

                    var cleaned = newValue.replace("+56", "").replace(" ", "")
                    if (cleaned.length > 9) cleaned = cleaned.take(9)

                    phone = "+56 " + cleaned.chunked(1).joinToString(" ")
                },
                label = { Text("Teléfono (+56 9 XXXX XXXX)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // -------- GUARDAR TELÉFONO --------
            Button(
                onClick = {
                    val cleanedPhone = phone
                        .replace("+56", "")
                        .replace(" ", "")
                        .filter { it.isDigit() }

                    if (cleanedPhone.length != 9) {
                        errorMessage = "Formato de teléfono inválido"
                    } else {
                        viewModel.updateUserPhone(cleanedPhone)
                        successMessage = "Teléfono actualizado correctamente"
                        errorMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Teléfono")
            }

            // =======================================================
            // CAMBIO DE CONTRASEÑA
            // =======================================================

            Divider()
            Text("Cambiar Contraseña", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = currentPass,
                onValueChange = { currentPass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña Actual") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                }
            )

            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Confirmar Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                }
            )

            Button(
                onClick = {
                    errorMessage = ""
                    successMessage = ""

                    if (currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios"
                        return@Button
                    }

                    if (newPass != confirmPass) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }

                    viewModel.changePassword(currentPass, newPass, confirmPass)
                    successMessage = "Contraseña actualizada correctamente"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Contraseña")
            }

            // =======================================================
            // MENSAJES DE ERROR / ÉXITO
            // =======================================================

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            if (successMessage.isNotEmpty()) {
                Text(successMessage, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
