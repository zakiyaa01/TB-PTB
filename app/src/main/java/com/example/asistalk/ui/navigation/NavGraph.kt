package com.example.asistalk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.asistalk.ui.MainScreen
import com.example.asistalk.ui.asishub.AsisHubScreen
import com.example.asistalk.ui.asislearn.AsisLearnScreen
import com.example.asistalk.ui.auth.LoginScreen
import com.example.asistalk.ui.auth.RegisterScreen
import com.example.asistalk.ui.home.HomeScreen
import com.example.asistalk.ui.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION,
        modifier = modifier
    ) {
        authNavGraph(navController)

        // Panggil MainScreen sebagai satu layar besar setelah login berhasil
        composable(route = Graph.MAIN) {
            MainScreen()
        }
    }
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "login",
        route = Graph.AUTHENTICATION
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// FUNGSI INI SEKARANG MENJADI NAVHOST UNTUK KONTEN UTAMA
@Composable
fun mainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        // --- PERBAIKAN UTAMA ADA DI SINI ---
        // Teruskan 'navController' ke SETIAP fungsi layar
        composable("home") { HomeScreen(navController = navController) }
        composable("asishub") { AsisHubScreen(navController = navController) }
        composable("asislearn") { AsisLearnScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}
