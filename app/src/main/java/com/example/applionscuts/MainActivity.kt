// Archivo: com/example/applionscuts/MainActivity.kt
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
import com.example.applionscuts.navigation.AppNavigation
import com.example.applionscuts.ui.screen.CartDialog
import com.example.applionscuts.ui.screen.PaymentDialog
import com.example.applionscuts.ui.theme.AppLionsCutsTheme
import com.example.applionscuts.data.local.database.AppDatabase
import com.example.applionscuts.ui.theme.viewmodel.AuthViewModelFactory
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModelFactory
import com.example.applionscuts.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppLionsCutsTheme {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(database.userDao())
                )
                val productViewModel: ProductViewModel = viewModel(
                    factory = ProductViewModelFactory(database.productDao())
                )

                val haircutViewModel: HaircutViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()
                val bookingViewModel: BookingViewModel = viewModel()

                val toastMessage by productViewModel.toastMessage.observeAsState()
                LaunchedEffect(toastMessage) {
                    toastMessage?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        productViewModel.onToastShown()
                    }
                }

                val navController = rememberNavController()
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
                        bookingViewModel = bookingViewModel
                    )
                }

                val showCart by productViewModel.showCartDialog.observeAsState(false)
                val showPayment by productViewModel.showPaymentDialog.observeAsState(false)
                if (showCart) CartDialog(viewModel = productViewModel)
                if (showPayment) PaymentDialog(viewModel = productViewModel)
            }
        }
    }
}