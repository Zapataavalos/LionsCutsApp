package com.example.applionscuts.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.applionscuts.R
import com.example.applionscuts.model.UserRole
import com.example.applionscuts.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }
    var barberData by remember { mutableStateOf("") }

    val registrationResult by authViewModel.registrationSuccess.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    LaunchedEffect(registrationResult) {
        if (registrationResult == true) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.leon),
                contentDescription = "Logo LionsCuts",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Registro", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = name,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                        name = newValue
                    }
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    // Solo permite dígitos y espacios
                    val digitsOnly = newValue.filter { it.isDigit() }
                    // Aplicar formato chileno: 9 XXXX XXXX (máx 9 dígitos)
                    if (digitsOnly.length <= 9) {
                        phone = when (digitsOnly.length) {
                            in 1..4 -> digitsOnly
                            in 5..8 -> "${digitsOnly.take(4)} ${digitsOnly.drop(4)}"
                            9 -> "${digitsOnly.take(1)} ${digitsOnly.substring(1, 5)} ${digitsOnly.substring(5)}"
                            else -> digitsOnly
                        }
                    }
                },
                label = { Text("Teléfono (ej: 9 1234 5678)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Soy:")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedRole == UserRole.CLIENT,
                    onClick = { selectedRole = UserRole.CLIENT }
                )
                Text("Cliente", Modifier.padding(start = 4.dp))
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = selectedRole == UserRole.BARBER,
                    onClick = { selectedRole = UserRole.BARBER }
                )
                Text("Barbero", Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (selectedRole == UserRole.BARBER) {
                OutlinedTextField(
                    value = barberData,
                    onValueChange = { barberData = it },
                    label = { Text("Especialidad y Años de Experiencia") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    authViewModel.register(
                        name = name,
                        email = email,
                        phone = phone,
                        password = password,
                        confirmPassword = confirmPassword,
                        role = selectedRole,
                        barberSpecificData = barberData
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}