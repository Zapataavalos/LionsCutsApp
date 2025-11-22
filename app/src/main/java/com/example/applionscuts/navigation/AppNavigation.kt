package com.example.applionscuts.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.applionscuts.ui.screen.*
import com.example.applionscuts.ui.theme.screen.ProductsScreen
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    haircutViewModel: HaircutViewModel,
    profileViewModel: ProfileViewModel,
    bookingViewModel: BookingViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(false)
    val isAdmin by authViewModel.isAdmin.observeAsState(false)
    val currentUser by authViewModel.currentUser.observeAsState()

    NavHost(
        navController = navController,
        startDestination =
            if (isLoggedIn) {
                if (isAdmin == true) Routes.Admin else Routes.Home
            } else Routes.Login
    ) {

        // --- LOGIN ---
        composable(Routes.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    if (authViewModel.isAdmin.value == true) {
                        navController.navigate(Routes.Admin) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.Register) }
            )
        }

        // --- REGISTER ---
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

        // --- HOME ---
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

        // --- PRODUCTS ---
        composable(Routes.Products) {
            ProductsScreen(
                productViewModel = productViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- HAIRCUTS ---
        composable(Routes.Haircuts) {
            HaircutsScreen(
                viewModel = haircutViewModel,
                onBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }

        // --- PROFILE ---
        composable(Routes.Profile) {

            val userIdStr = currentUser?.id?.toString() ?: ""
            val userName = currentUser?.name ?: ""

            ProfileScreen(
                viewModel = profileViewModel,
                productViewModel = productViewModel,
                bookingViewModel = bookingViewModel,   // agregado
                currentUserId = userIdStr,             // agregado
                currentUserName = userName,            // agregado
                onBack = { navController.popBackStack() }
            )
        }

        // --- BOOKING ---
        composable(Routes.Booking) {

            val userIdStr = currentUser?.id?.toString() ?: ""
            val userName = currentUser?.name ?: ""

            BookingScreen(
                viewModel = bookingViewModel,
                currentUserId = userIdStr,
                currentUserName = userName,
                onBack = { navController.popBackStack() }
            )
        }

        // --- ADMIN ---
        composable(Routes.Admin) {
            AdminScreen(
                productViewModel = productViewModel,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }

    LaunchedEffect(isAdmin, isLoggedIn) {
        if (isLoggedIn && isAdmin == true) {
            navController.navigate(Routes.Admin) {
                popUpTo(Routes.Login) { inclusive = true }
            }
        }
    }
}
