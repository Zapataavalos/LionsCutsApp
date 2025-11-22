package com.example.applionscuts.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.applionscuts.viewmodel.AuthViewModel
import com.example.applionscuts.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel,   // ← NECESARIO para actualizar el nombre global
    onBack: () -> Unit
) {
    val user = viewModel.userProfile.observeAsState().value

    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf("+56 9 ") }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cargar teléfono del usuario
    LaunchedEffect(user) {
        user?.phone?.let { saved ->
            val formatted = saved.chunked(1).joinToString(" ")
            phone = "+56 $formatted"
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Datos Personales", style = MaterialTheme.typography.titleMedium)

            // ----------------- CAMBIO DE NOMBRE -----------------
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.all { c -> c.isLetter() || c.isWhitespace() })
                        name = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") }
            )

            Button(
                onClick = {
                    scope.launch {
                        if (name.isBlank()) {
                            snackbarHostState.showSnackbar("El nombre no puede estar vacío")
                        } else {
                            viewModel.updateUserName(name)
                            authViewModel.updateCurrentUserName(name)
                            snackbarHostState.showSnackbar("Nombre actualizado")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Nombre")
            }


            // ----------------- CAMBIO DE TELÉFONO -----------------
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

            Button(
                onClick = {
                    scope.launch {
                        val cleanedPhone = phone
                            .replace("+56", "")
                            .replace(" ", "")
                            .filter { it.isDigit() }

                        if (cleanedPhone.length != 9) {
                            snackbarHostState.showSnackbar("Formato de teléfono inválido")
                        } else {
                            viewModel.updateUserPhone(cleanedPhone)
                            snackbarHostState.showSnackbar("Teléfono actualizado")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Teléfono")
            }

            Divider()
            Text("Cambiar Contraseña", style = MaterialTheme.typography.titleMedium)

            // ---------------- CONTRASEÑA ACTUAL ----------------
            OutlinedTextField(
                value = currentPass,
                onValueChange = { currentPass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña Actual") },
                visualTransformation = PasswordVisualTransformation()
            )

            // ---------------- NUEVA CONTRASEÑA ----------------
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
                    scope.launch {
                        when {
                            currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank() ->
                                snackbarHostState.showSnackbar("Todos los campos son obligatorios")

                            newPass != confirmPass ->
                                snackbarHostState.showSnackbar("Las contraseñas no coinciden")

                            else -> {
                                viewModel.changePassword(currentPass, newPass, confirmPass)
                                snackbarHostState.showSnackbar("Contraseña actualizada")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Contraseña")
            }
        }
    }
}

