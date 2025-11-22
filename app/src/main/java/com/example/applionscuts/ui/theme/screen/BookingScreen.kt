package com.example.applionscuts.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.applionscuts.R
import com.example.applionscuts.model.Barber
import com.example.applionscuts.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    currentUserId: String,
    currentUserName: String,
    onBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.setUserData(currentUserId, currentUserName)
    }

    val barbers by viewModel.barbers.observeAsState(emptyList())
    val dates by viewModel.availableDates.observeAsState(emptyList())
    val times by viewModel.availableTimes.observeAsState(emptyList())

    val selectedBarber by viewModel.selectedBarber.observeAsState()
    val selectedDate by viewModel.selectedDate.observeAsState()
    val selectedTime by viewModel.selectedTime.observeAsState()

    val bookingSuccess by viewModel.bookingSuccess.observeAsState(false)

    val context = LocalContext.current

    LaunchedEffect(bookingSuccess) {
        if (bookingSuccess) {
            Toast.makeText(context, "¡Cita Agendada!", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.booking_background),
                contentDescription = "Fondo de barbería",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "1. Elige tu Barbero",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                BarberSelector(
                    barbers = barbers,
                    selectedBarber = selectedBarber,
                    onBarberSelected = { viewModel.onBarberSelected(it) }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "2. Elige el Día",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateSelector(
                    dates = dates,
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.onDateSelected(it) }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "3. Elige la Hora",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeSelector(
                    times = times,
                    selectedTime = selectedTime,
                    onTimeSelected = { viewModel.onTimeSelected(it) }
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.confirmBooking() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = (selectedBarber != null && selectedDate != null && selectedTime != null)
                ) {
                    Text("Confirmar Cita")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberSelector(
    barbers: List<Barber>,
    selectedBarber: Barber?,
    onBarberSelected: (Barber) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedBarber?.name ?: "Selecciona un barbero",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            barbers.forEach { barber ->
                DropdownMenuItem(
                    text = { Text(barber.name) },
                    onClick = {
                        onBarberSelected(barber)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    dates: List<String>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            SuggestionChip(
                onClick = { onDateSelected(date) },
                label = { Text(date) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    times: List<String>,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.heightIn(max = 200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(times) { time ->
            val isSelected = time == selectedTime
            SuggestionChip(
                onClick = { onTimeSelected(time) },
                label = { Text(time) },
                modifier = Modifier.fillMaxWidth(),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
