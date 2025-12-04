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
import com.example.asistalk.ui.asislearn.AsisLearnViewModel // <-- IMPOR TAMBAHAN PENTING
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import com.example.asistalk.ui.asislearn.LihatScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

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


        // =================================================================
        // PERBAIKAN: GRAFIK BERSARANG UNTUK ASISLEARN AGAR BERBAGI VIEWMODEL
        // =================================================================
        navigation(
            startDestination = "asislearn_main",
            route = "asislearn" // Rute ini yang dipanggil oleh Bottom Bar
        ) {

            // 1. ASISLEARNSCREEN (Daftar Materi)
            composable("asislearn_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                // Menggunakan ViewModel yang terikat pada scope "asislearn" (Instance Tunggal)
                val asisLearnViewModel: AsisLearnViewModel = viewModel(parentEntry)

                // MEMASUKKAN VIEWMODEL KE DALAM COMPOSABLE
                AsisLearnScreen(navController = navController, viewModel = asisLearnViewModel)
            }

            // 2. UPLOADMATERIALSCREEN (Form Upload)
            composable("uploadMaterial") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                // Menggunakan ViewModel yang SAMA
                val asisLearnViewModel: AsisLearnViewModel = viewModel(parentEntry)

                // MEMASUKKAN VIEWMODEL KE DALAM COMPOSABLE
                UploadMaterialScreen(navController = navController, viewModel = asisLearnViewModel)
            }

            // 3. LIHATSCREEN / DETAIL MATERI (Menampilkan detail)
            composable(
                route = "materialDetail/{materialTitle}", // Rute dengan argumen
                arguments = listOf(navArgument("materialTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                // Menggunakan ViewModel yang SAMA
                val asisLearnViewModel: AsisLearnViewModel = viewModel(parentEntry)

                // Mengambil argumen judul
                val materialTitle = backStackEntry.arguments?.getString("materialTitle") ?: ""

                // MEMASUKKAN VIEWMODEL DAN ARGUMEN KE DALAM COMPOSABLE
                LihatScreen(
                    navController = navController,
                    viewModel = asisLearnViewModel,
                    materialTitle = materialTitle
                )
            }
        }


        // =================================================================
        // GRAFIK BERSARANG UNTUK ASISHUB (Sudah Benar)
        // =================================================================
        navigation(
            startDestination = "asishub_main", // Layar utama di dalam grup AsisHub
            route = "asishub" // Rute ini yang dipanggil oleh Bottom Bar
        ) {

            composable("asishub_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
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
            composable("notif") {
                NotificationScreen(navController = navController)
            }

        }
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}