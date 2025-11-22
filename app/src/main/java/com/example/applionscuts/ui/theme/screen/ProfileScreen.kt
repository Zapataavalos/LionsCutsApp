// Archivo: com/example/applionscuts/ui/screen/ProfileScreen.kt
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import com.example.applionscuts.R
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.model.UserProfile
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.BookingViewModel
import com.example.applionscuts.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    productViewModel: ProductViewModel,
    currentUserId: String,
    currentUserName: String,
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.observeAsState()
    val appointments by viewModel.appointments.observeAsState(emptyList())
    val showDialog by viewModel.showRedeemDialog.observeAsState(false)
    val showChangePasswordDialog by viewModel.showChangePasswordDialog.observeAsState(false)
    val selectedImageUri by viewModel.selectedImageUri.observeAsState()

    val context = LocalContext.current
    var showImageOptions by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }




    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            photoUri?.let { uri ->
                viewModel.onImageSelected(uri)
            }
        }
    }

    // Launcher para la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    // Launcher para solicitar permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido → abrir cámara
            val photoFile = File(context.cacheDir, "profile_image.jpg")
            photoUri = getUriForFile(context, photoFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            cameraLauncher.launch(intent)
        } else {
            // Si se niega el permiso, abrir galería
            galleryLauncher.launch("image/*")
        }
    }

    if (showDialog) {
        RedeemRewardDialog(
            onDismiss = { viewModel.onDialogDismiss() },
            onRedeemCut = { viewModel.onRedeemConfirmed("Corte Gratis") },
            onRedeemProduct = { viewModel.onRedeemConfirmed("Producto Gratis") }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { viewModel.onHideChangePasswordDialog() },
            onChangePassword = { currentPass, newPass, confirmPass ->
                viewModel.changePassword(currentPass, newPass, confirmPass)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { productViewModel.onShowCart() }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileHeader(
                    user = userProfile ?: UserProfile("", "", "", 0),
                    selectedImageUri = selectedImageUri,
                    onImageClick = { showImageOptions = true }
                )
                Spacer(Modifier.height(24.dp))
            }
            item {
                userProfile?.let {
                    FidelitySection(stars = it.fidelityStars, onRedeemClick = { viewModel.onRedeemClicked() })
                }
                Spacer(Modifier.height(24.dp))
            }
            item {
                AppointmentsSection(appointments = appointments)
                Spacer(Modifier.height(24.dp))
            }
            item {
                Button(
                    onClick = { viewModel.onShowChangePasswordDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Cambiar Contraseña", textAlign = TextAlign.Center)
                }
            }
        }
    }

    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Cambiar Foto de Perfil") },
            text = { Text("¿Cómo quieres cambiar tu foto?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageOptions = false
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        val photoFile = File(context.cacheDir, "profile_image.jpg")
                        photoUri = getUriForFile(context, photoFile)
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        }
                        cameraLauncher.launch(intent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text("Tomar Foto")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageOptions = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Elegir de Galería")
                }
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
                    modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.leon),
                    contentDescription = "Foto de perfil por defecto",
                    modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(
                onClick = onImageClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Cambiar foto",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onChangePassword: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Contraseña") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña Actual") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = { Text("Confirmar Nueva Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                if (errorMessage.isNotBlank()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                errorMessage = ""
                if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                    errorMessage = "Todos los campos son obligatorios"
                    return@Button
                }
                if (newPassword != confirmNewPassword) {
                    errorMessage = "Las nuevas contraseñas no coinciden"
                    return@Button
                }
                onChangePassword(currentPassword, newPassword, confirmNewPassword)
            }) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun getUriForFile(context: android.content.Context, file: File): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } else {
        Uri.fromFile(file)
    }
}

@Composable
fun FidelitySection(stars: Int, onRedeemClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Estrellas de Fidelidad: $stars", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 8.dp)) {
            repeat(stars) { Icon(Icons.Default.Star, contentDescription = "Estrella de fidelidad", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) }
        }
        if (stars == 10) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRedeemClick) { Text("Canjear Recompensa") }
        }
    }
}

@Composable
fun AppointmentsSection(appointments: List<AppointmentEntity>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Próximas Citas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (appointments.isEmpty()) {
            Text("No tienes citas próximas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            appointments.forEach { appointment ->
                AppointmentItem(appointment)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: AppointmentEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${appointment.service} con ${appointment.barberName}")
            Text("${appointment.date} a las ${appointment.time}")
        }
    }
}



@Composable
fun RedeemRewardDialog(onDismiss: () -> Unit, onRedeemCut: () -> Unit, onRedeemProduct: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Canjear Recompensa", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("¡Has acumulado 10 estrellas! Elige tu recompensa:", textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRedeemCut, modifier = Modifier.fillMaxWidth()) { Text("Canjear por un Corte Gratis") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRedeemProduct, modifier = Modifier.fillMaxWidth()) { Text("Canjear por un Producto Gratis") }
            }
        },
        confirmButton = { },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}