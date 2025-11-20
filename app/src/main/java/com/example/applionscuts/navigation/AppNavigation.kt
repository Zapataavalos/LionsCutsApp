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

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) {
            if (isAdmin == true) Routes.Admin else Routes.Home
        } else {
            Routes.Login
        }
    ) {
        composable(Routes.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register)
                }
            )
        }

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

        composable(Routes.Home) {
            HomeScreen(
                onNavigateToHaircuts = { navController.navigate(Routes.Haircuts) },
                onNavigateToProducts = { navController.navigate(Routes.Products) },
                onNavigateToProfile = { navController.navigate(Routes.Profile) },
                onNavigateToBooking = { navController.navigate(Routes.Booking) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                productViewModel = productViewModel
            )
        }

        composable(Routes.Products) {
            ProductsScreen(
                productViewModel = productViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Haircuts) {
            HaircutsScreen(
                viewModel = haircutViewModel,
                onBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }

        composable(Routes.Profile) {
            val currentUserName by authViewModel.currentUserName.observeAsState("")
            profileViewModel.setUserEmail(currentUserName)
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }

        composable(Routes.Booking) {
            BookingScreen(
                viewModel = bookingViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Admin) {
            AdminScreen(
                productViewModel = productViewModel,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }

    LaunchedEffect(isAdmin) {
        if (isAdmin == true) {
            navController.navigate(Routes.Admin) {
                popUpTo(Routes.Login) { inclusive = true }
            }
        }
    }
}