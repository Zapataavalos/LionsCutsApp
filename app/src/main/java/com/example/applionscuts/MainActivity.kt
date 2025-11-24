package com.example.applionscuts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.applionscuts.data.client.usuarios.AuthApi
import com.example.applionscuts.data.client.usuarios.AuthClient
import com.example.applionscuts.data.client.usuarios.UsuariosApi
import com.example.applionscuts.data.client.usuarios.UsuariosClient
import com.example.applionscuts.navigation.AppNavigation
import com.example.applionscuts.ui.screen.CartDialog
import com.example.applionscuts.ui.screen.PaymentDialog
import com.example.applionscuts.ui.theme.AppLionsCutsTheme
import com.example.applionscuts.data.local.database.AppDatabase

// FACTORIES
import com.example.applionscuts.ui.theme.viewmodel.AuthViewModelFactory
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModelFactory
import com.example.applionscuts.viewmodel.PurchaseViewModelFactory

// VIEWMODELS
import com.example.applionscuts.viewmodel.*
import com.example.applionscuts.ui.theme.viewmodel.BarberViewModel
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.data.repository.PurchaseRepository
import com.example.applionscuts.data.repository.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppLionsCutsTheme {

                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)

                //integracion dao
                val userDao = database.userDao()
                // ^ Obtenemos el DAO de usuarios desde la DB.

                //integracion api

                val usuariosApi = UsuariosClient.create(UsuariosApi::class.java)

                val authApi = AuthClient.create(AuthApi::class.java)

                //integracion repository

                val userRepository = UserRepository(userDao,usuariosApi,authApi)

                // ---------------- AUTH ----------------
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(userRepository)
                )

                // ---------------- PURCHASES (ROOM + REPO + FACTORY) ----------------
                val purchaseRepository = PurchaseRepository(database.purchaseDao())

                val purchaseViewModel: PurchaseViewModel = viewModel(
                    factory = PurchaseViewModelFactory(purchaseRepository)
                )

                // ---------------- PRODUCTS (con purchaseRepository) ----------------
                val productViewModel: ProductViewModel = viewModel(
                    factory = ProductViewModelFactory(
                        database.productDao(),
                        purchaseRepository     // ⭐ requerido ahora
                    )
                )

                // ---------------- OTROS VIEWMODELS ----------------
                val barberViewModel: BarberViewModel = viewModel()
                val haircutViewModel: HaircutViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()
                val bookingViewModel: BookingViewModel = viewModel()

                // ---------------- TOASTS ----------------
                val toastMessage by productViewModel.toastMessage.observeAsState()
                LaunchedEffect(toastMessage) {
                    toastMessage?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        productViewModel.onToastShown()
                    }
                }

                // ---------------- NAV CONTROLLER ----------------
                val navController = rememberNavController()

                // ---------------- UI ROOT ----------------
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        productViewModel = productViewModel,
                        haircutViewModel = haircutViewModel,
                        profileViewModel = profileViewModel,
                        bookingViewModel = bookingViewModel,
                        barberViewModel = barberViewModel,
                        purchaseViewModel = purchaseViewModel
                    )
                }

                // ---------------- CART DIALOG ----------------
                val showCart by productViewModel.showCartDialog.observeAsState(false)
                if (showCart) {
                    CartDialog(viewModel = productViewModel)
                }

                // ---------------- PAYMENT DIALOG ----------------
                val showPayment by productViewModel.showPaymentDialog.observeAsState(false)
                if (showPayment) {
                    PaymentDialog(
                        viewModel = productViewModel,
                        purchaseViewModel = purchaseViewModel,
                        currentUserId = authViewModel.currentUser.value?.id ?: 0
                    )
                }
            }
        }
    }
}
