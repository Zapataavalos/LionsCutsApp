package com.example.applionscuts.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.applionscuts.ui.theme.theme.LionsCutsBlack
import com.example.applionscuts.ui.theme.theme.LionsCutsYellow
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(viewModel: ProductViewModel) {
    var nombre by remember { mutableStateOf("") }
    var dosApellidos by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var metodoEntrega by remember { mutableStateOf("Retiro en Tienda") }
    var direccion by remember { mutableStateOf("") }

    val totalPrice by viewModel.totalPrice.observeAsState(0.0)

    val chileLocale = Locale("es", "CL")
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(chileLocale) }
    currencyFormat.maximumFractionDigits = 0
    val formattedPrice = currencyFormat.format(totalPrice)

    Dialog(onDismissRequest = { viewModel.onHidePayment() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LionsCutsBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Formulario de Pago",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    IconButton(onClick = { viewModel.onHidePayment() }) {
                        Icon(Icons.Default.Close, "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = LionsCutsYellow,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = LionsCutsYellow,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = LionsCutsYellow,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$"))) {
                            nombre = newValue
                        }
                    },
                    label = { Text("Nombre", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dosApellidos,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$"))) {
                            dosApellidos = newValue
                        }
                    },
                    label = { Text("Dos Apellidos", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rut,
                    onValueChange = { newValue ->
                        val filteredValue = newValue.filter {
                            it.isDigit() || it.equals('k', ignoreCase = true) || it == '.' || it == '-'
                        }
                        if (filteredValue.length <= 12) {
                            rut = filteredValue.uppercase()
                        }
                    },
                    label = { Text("RUT", color = Color.White.copy(alpha = 0.7f)) },
                    placeholder = { Text("12.345.678-K", color = Color.White.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = numeroTarjeta,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }
                            if (filtered.length <= 16) {
                                numeroTarjeta = filtered
                            }
                        },
                        label = {
                            Text(
                                "Número de Tarjeta",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        placeholder = {
                            Text("**** **** **** ****", color = Color.White.copy(alpha = 0.5f))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(0.7f),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { newValue ->
                            if (newValue.length <= 3 && newValue.all { it.isDigit() }) {
                                cvv = newValue
                            }
                        },
                        label = { Text("CVV", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("123", color = Color.White.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(0.3f),
                        colors = textFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Método de Entrega",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = metodoEntrega == "Retiro en Tienda",
                        onClick = { metodoEntrega = "Retiro en Tienda" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = LionsCutsYellow,
                            unselectedColor = Color.White
                        )
                    )
                    Text("Retiro en Tienda", color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = metodoEntrega == "Envío a Domicilio",
                        onClick = { metodoEntrega = "Envío a Domicilio" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = LionsCutsYellow,
                            unselectedColor = Color.White
                        )
                    )
                    Text("Envío a Domicilio", color = Color.White)
                }

                AnimatedVisibility(
                    visible = metodoEntrega == "Envío a Domicilio",
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { direccion = it },
                            label = {
                                Text(
                                    "Dirección de Envío",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            },
                            placeholder = {
                                Text("Calle, Número, Comuna", color = Color.White.copy(alpha = 0.5f))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.confirmPayment(
                            nombre,
                            dosApellidos,
                            rut,
                            numeroTarjeta,
                            cvv,
                            metodoEntrega,
                            if (metodoEntrega == "Envío a Domicilio") direccion else null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LionsCutsYellow,
                        contentColor = LionsCutsBlack
                    ),
                    enabled = nombre.isNotBlank() &&
                            dosApellidos.isNotBlank() &&
                            rut.isNotBlank() &&
                            numeroTarjeta.length >= 15 &&
                            cvv.length == 3 &&
                            (metodoEntrega == "Retiro en Tienda" ||
                                    (metodoEntrega == "Envío a Domicilio" && direccion.isNotBlank()))
                ) {
                    Text(
                        "Pagar ($formattedPrice)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        viewModel.onHidePayment()
                        viewModel.onShowCart()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Carrito", color = Color.White)
                }
            }
        }
    }
}
