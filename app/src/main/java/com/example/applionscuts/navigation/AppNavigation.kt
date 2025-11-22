package com.example.applionscuts.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.applionscuts.ui.screen.*
import com.example.applionscuts.ui.theme.screen.ProductsScreen
import com.example.applionscuts.ui.theme.viewmodel.BarberViewModel
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    haircutViewModel: HaircutViewModel,
    profileViewModel: ProfileViewModel,
    bookingViewModel: BookingViewModel,
    barberViewModel: BarberViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(false)
    val currentUser by authViewModel.currentUser.observeAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.Home else Routes.Login
    ) {

        // LOGIN
        composable(Routes.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.Register) }
            )
        }

        // REGISTER
        composable(Routes.Register) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

        // HOME
        composable(Routes.Home) {
            HomeScreen(
                onNavigateToHaircuts = { navController.navigate(Routes.Haircuts) },
                onNavigateToProducts = { navController.navigate(Routes.Products) },
                onNavigateToProfile = { navController.navigate(Routes.Profile) },
                onNavigateToBooking = { navController.navigate(Routes.Booking) },
                onNavigateToAdmin = { navController.navigate(Routes.Admin) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                productViewModel = productViewModel,
                authViewModel = authViewModel
            )
        }

        // PRODUCTS
        composable(Routes.Products) {
            ProductsScreen(
                productViewModel = productViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // HAIRCUTS
        composable(Routes.Haircuts) {
            HaircutsScreen(
                viewModel = haircutViewModel,
                onBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }

        // PROFILE
        composable(Routes.Profile) {

            val user = currentUser

            // Sincronizar datos
            LaunchedEffect(user) {
                if (user != null) {
                    profileViewModel.updateUserFromAuth(
                        id = user.id.toString(),
                        name = user.name,
                        email = user.email,
                        phone = user.phone
                    )
                }
            }

            ProfileScreen(
                viewModel = profileViewModel,
                productViewModel = productViewModel,
                bookingViewModel = bookingViewModel,
                currentUserId = user?.id?.toString() ?: "",
                currentUserName = user?.name ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToEditProfile = {
                    navController.navigate(Routes.ProfileEdit)
                }
            )
        }

        // PROFILE EDIT
        composable(Routes.ProfileEdit) {
            ProfileEditScreen(
                viewModel = profileViewModel,
                authViewModel = authViewModel,  // ⭐ Se pasa correctamente
                onBack = { navController.popBackStack() }
            )
        }

        // BOOKING
        composable(Routes.Booking) {
            val user = currentUser

            BookingScreen(
                viewModel = bookingViewModel,
                currentUserId = user?.id?.toString() ?: "",
                currentUserName = user?.name ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        // ADMIN
        composable(Routes.Admin) {
            AdminScreen(
                productViewModel = productViewModel,
                bookingViewModel = bookingViewModel,
                barberViewModel = barberViewModel,
                haircutViewModel = haircutViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
