package com.example.applionscuts.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.applionscuts.R
import com.example.applionscuts.ui.components.AppDrawer
import com.example.applionscuts.ui.theme.components.AppTopBar
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHaircuts: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onLogout: () -> Unit,
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val userName by authViewModel.currentUserName.observeAsState("Usuario")
    var showWelcomeMessage by remember { mutableStateOf(true) }

    LaunchedEffect(showWelcomeMessage) {
        if (showWelcomeMessage) {
            Toast.makeText(context, "¡Bienvenido, $userName!", Toast.LENGTH_SHORT).show()
            showWelcomeMessage = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onNavigateToHaircuts = onNavigateToHaircuts,
                onNavigateToProducts = onNavigateToProducts,
                onNavigateToProfile = onNavigateToProfile,
                onLogout = onLogout
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "LionsCuts",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCartClick = { productViewModel.onShowCart() }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.leons),
                    contentDescription = "Fondo de barbería",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Agenda tu cita en",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "LIONS CUTS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "El mejor estilo y servicio en un solo lugar. Calidad y experiencia a tu alcance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onNavigateToBooking) {
                        Text(text = "Agendar Cita")
                    }
                }
            }
        }
    }
}