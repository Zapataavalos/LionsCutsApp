package com.example.applionscuts.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.applionscuts.R
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.model.UserProfile
import com.example.applionscuts.ui.theme.components.AppTopBar
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.BookingViewModel
import com.example.applionscuts.viewmodel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    productViewModel: ProductViewModel,
    currentUserId: String,
    currentUserName: String,
    bookingViewModel: BookingViewModel,
    onNavigateToEditProfile: () -> Unit,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.observeAsState()
    val appointments by viewModel.appointments.observeAsState(emptyList())
    val showDialog by viewModel.showRedeemDialog.observeAsState(false)
    val selectedImageUri by viewModel.selectedImageUri.observeAsState()

    val context = LocalContext.current
    var showImageOptions by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Sincronizar citas
    val bookingAppointments by bookingViewModel.appointments.observeAsState(emptyList())
    LaunchedEffect(bookingAppointments) {
        viewModel.setAppointments(bookingAppointments)
    }

    // ----------- LANZADORES DE FOTO -----------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == android.app.Activity.RESULT_OK) {
            photoUri?.let { uri -> viewModel.onImageSelected(uri) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.onImageSelected(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "profile_image.jpg")
            photoUri = getUriForFile(context, file)
            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            })
        } else galleryLauncher.launch("image/*")
    }

    // ----------- DIALOGO RECOMPENSA -----------
    if (showDialog) {
        RedeemRewardDialog(
            onDismiss = { viewModel.onDialogDismiss() },
            onRedeemCut = { viewModel.onRedeemConfirmed("Corte Gratis") },
            onRedeemProduct = { viewModel.onRedeemConfirmed("Producto Gratis") }
        )
    }

    // ================ UI PRINCIPAL =================
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mi Perfil",
                showBackButton = true,
                onBackClick = onBack,
                onCartClick = { productViewModel.onShowCart() },
                extraActions = {
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(Icons.Default.Settings, contentDescription = "Editar Perfil")
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {

            item {
                ProfileHeader(
                    user = userProfile ?: UserProfile("", "", "", "", 0),
                    selectedImageUri = selectedImageUri,
                    onImageClick = { showImageOptions = true }
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                userProfile?.let {
                    FidelitySection(
                        stars = it.fidelityStars,
                        onRedeemClick = { viewModel.onRedeemClicked() }
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                AppointmentsSection(appointments)
            }
        }
    }

    // ----------- DIALOGO DE FOTO -----------
    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Cambiar Foto de Perfil") },
            text = { Text("Selecciona una opción") },
            confirmButton = {
                TextButton(onClick = {
                    showImageOptions = false
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        val file = File(context.cacheDir, "profile_image.jpg")
                        photoUri = getUriForFile(context, file)
                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        })
                    } else permissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Tomar Foto") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageOptions = false
                    galleryLauncher.launch("image/*")
                }) { Text("Elegir de Galería") }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    user: UserProfile,
    selectedImageUri: Uri?,
    onImageClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(90.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.leon),
                    contentDescription = "Foto",
                    modifier = Modifier.size(90.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            IconButton(
                onClick = onImageClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
            ) {
                Icon(Icons.Default.Camera, "Cambiar foto")
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(user.name.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(user.email.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun FidelitySection(stars: Int, onRedeemClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Estrellas de Fidelidad: $stars",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row {
            repeat(stars) {
                Icon(Icons.Default.Star, "estrella", tint = MaterialTheme.colorScheme.primary)
            }
        }

        if (stars >= 10) {
            Button(onClick = onRedeemClick) {
                Text("Canjear Recompensa")
            }
        }
    }
}

@Composable
fun AppointmentsSection(appointments: List<AppointmentEntity>) {
    Column {
        Text("Próximas Citas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        if (appointments.isEmpty()) {
            Text("No tienes citas próximas")
        } else {
            appointments.forEach { AppointmentItem(it) }
        }
    }
}

@Composable
fun AppointmentItem(appointment: AppointmentEntity) {

    val formattedDate = try {
        val p = appointment.date.split("-")
        "${p[2]}-${p[1]}-${p[0]}"
    } catch (e: Exception) { appointment.date }

    val formattedTime = try {
        val hour = appointment.time.substringBefore(":").toInt()
        val min = appointment.time.substringAfter(":")
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        "$hour12:$min $amPm"
    } catch (e: Exception) { appointment.time }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("${appointment.service} con ${appointment.barberName}")
            Text(formattedDate)
            Text(formattedTime)
        }
    }
}

@Composable
fun RedeemRewardDialog(
    onDismiss: () -> Unit,
    onRedeemCut: () -> Unit,
    onRedeemProduct: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Canjear Recompensa") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Has alcanzado 10 estrellas.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRedeemCut, modifier = Modifier.fillMaxWidth()) {
                    Text("Corte Gratis")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRedeemProduct, modifier = Modifier.fillMaxWidth()) {
                    Text("Producto Gratis")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

private fun getUriForFile(context: android.content.Context, file: File): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } else Uri.fromFile(file)
}
