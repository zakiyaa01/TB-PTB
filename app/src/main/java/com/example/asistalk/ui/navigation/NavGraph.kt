package com.example.asistalk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.asistalk.ui.MainScreen
import com.example.asistalk.ui.asishub.AsisHubScreen
import com.example.asistalk.ui.asishub.CreatePostScreen
import com.example.asistalk.ui.asishub.EditPostScreen
import com.example.asistalk.ui.asishub.NotificationScreen
import com.example.asistalk.ui.asishub.PostDetailScreen
import com.example.asistalk.ui.asislearn.AsisLearnScreen
import com.example.asistalk.ui.auth.LoginScreen
import com.example.asistalk.ui.auth.RegisterScreen
import com.example.asistalk.ui.home.HomeScreen
import com.example.asistalk.ui.profile.ProfileScreen
import com.example.asistalk.ui.asislearn.UploadMaterialScreen
import com.example.asistalk.ui.asishub.AsisHubViewModel
import androidx.compose.runtime.remember
import com.example.asistalk.ui.profile.YourProfileScreen
import com.example.asistalk.ui.profile.SettingsScreen
import com.example.asistalk.ui.profile.AboutScreen

import androidx.navigation.NavBackStackEntry


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
        startDestination = "home", // Tujuan awal setelah login
        modifier = modifier
    ) {
        // --- Rute untuk Bottom Navigation Bar ---
        composable("home") { HomeScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }

        // --- Rute untuk AsisLearn dan halaman detailnya ---
        composable("asislearn") { AsisLearnScreen(navController = navController) }
        composable("uploadMaterial") { UploadMaterialScreen(navController = navController) } // Daftarkan rute upload di sini

        // --- BUAT GRAFIK BERSARANG UNTUK ASISHUB AGAR BERBAGI VIEWMODEL ---
        navigation(
            startDestination = "asishub_main", // Layar utama di dalam grup AsisHub
            route = "asishub" // Rute ini yang dipanggil oleh Bottom Bar
        ) {

            // --- PERBAIKAN ---
            composable("asishub_main") { backStackEntry ->
                // Dapatkan NavBackStackEntry dari grafik induk ("asishub")
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                // Buat ViewModel yang terikat pada grafik induk tersebut
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)

                AsisHubScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("createPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                CreatePostScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("postDetail") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                PostDetailScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("editPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                EditPostScreen(navController = navController, vm = asisHubViewModel)
            }
            // composable("notif") tidak perlu diubah karena tidak menggunakan ViewModel
            composable("notif") {
                NotificationScreen(navController = navController)
            }
            // RUTE UNTUK HALAMAN PROFIL

            composable(route = "yourProfile") {
                YourProfileScreen(navController = navController)
            }

            composable(route = "settings") {
                SettingsScreen(navController = navController)
            }

            composable(route = "about") {
                AboutScreen(navController = navController)
            }
        }
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}

