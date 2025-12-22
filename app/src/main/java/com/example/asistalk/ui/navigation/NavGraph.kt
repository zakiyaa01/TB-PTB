package com.example.asistalk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.asistalk.ui.MainScreen
import com.example.asistalk.ui.asishub.*
import com.example.asistalk.ui.asislearn.*
import com.example.asistalk.ui.auth.LoginScreen
import com.example.asistalk.ui.auth.RegisterScreen
import com.example.asistalk.ui.home.HomeScreen
import com.example.asistalk.ui.profile.*
import androidx.navigation.NavType
import androidx.navigation.compose.composable
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
        startDestination = "home",
        modifier = modifier
    ) {
        // HOME
        composable("home") { backStackEntry ->
            val asisLearnViewModel: AsisLearnViewModel = viewModel(backStackEntry)
            HomeScreen(navController = navController, viewModel = asisLearnViewModel)
        }

        // ============================================================
        // ASISLEARN GRAPH (Upload & Edit Digabung)
        // ============================================================
        navigation(
            startDestination = "asislearn_main",
            route = "asislearn"
        ) {
            // Screen Utama List Materi
            composable("asislearn_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)
                AsisLearnScreen(navController = navController, viewModel = vm)
            }

            // Screen Form (Digunakan untuk Upload DAN Edit)
            composable("uploadMaterial") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                // Token sementara, pastikan diambil dari session login asli
                val token =
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJzdWNpIiwiaWF0IjoxNzY2MzgwNjY1LCJleHAiOjE3NjY5ODU0NjV9.DeqL3KcxXLdZWJJUNXMKzykND85yyijwtbpWyWew8ls"

                UploadMaterialScreen(
                    navController = navController,
                    viewModel = vm,
                    token = token
                )
            }
            composable(
                route = "detailMaterial/{materialId}",
                arguments = listOf(
                    navArgument("materialId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                // 1. Ambil ID dari argumen
                val id = backStackEntry.arguments?.getInt("materialId") ?: 0

                // 2. Ambil Parent Entry agar ViewModel-nya sama (Shared) dengan List & Upload
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                // 3. Masukkan variabel 'vm' ke parameter viewModel
                MaterialDetailScreen(
                    materialId = id,
                    navController = navController,
                    viewModel = vm // Gunakan variabel 'vm', bukan kata kunci 'viewModel'
                )
            }
        }
        // ============================================================
        // ASISHUB & PROFILE GRAPH (Tetap Sama)
        // ============================================================
        navigation(startDestination = "asishub_main", route = "asishub") {
            composable("asishub_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("asishub") }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                AsisHubScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("createPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("asishub") }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                CreatePostScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("postDetail") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("asishub") }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                PostDetailScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("editPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("asishub") }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                EditPostScreen(navController = navController, vm = asisHubViewModel)
            }
            composable("notif") { NotificationScreen(navController = navController) }
        }

        navigation(startDestination = "profile_main", route = "profile") {
            composable("profile_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("profile") }
                val profileViewModel: ProfileViewModel = viewModel(parentEntry)
                ProfileScreen(navController = navController, profileViewModel = profileViewModel)
            }
            composable("yourProfile") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("profile") }
                val profileViewModel: ProfileViewModel = viewModel(parentEntry)
                YourProfileScreen(navController = navController, profileViewModel = profileViewModel)
            }
            composable("settings") { SettingsScreen(navController = navController) }
            composable("about") { AboutScreen(navController = navController) }
        }
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}