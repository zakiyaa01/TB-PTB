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
import com.example.asistalk.ui.profile.AboutScreen
import com.example.asistalk.ui.profile.ProfileScreen
import com.example.asistalk.ui.profile.ProfileViewModel
import com.example.asistalk.ui.profile.SettingsScreen
import com.example.asistalk.ui.profile.YourProfileScreen
import androidx.compose.ui.platform.LocalContext
import com.example.asistalk.network.RetrofitClient
import com.example.asistalk.ui.profile.ProfileRepository
import com.example.asistalk.ui.profile.ProfileViewModelFactory

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
        composable(route = Graph.MAIN) { backStackEntry ->
            val token = backStackEntry
                .savedStateHandle
                .get<String>("token") ?: ""

            MainScreen(token = token)
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
fun mainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

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
        // ASISLEARN GRAPH (Shared ViewModel - Sintaks Diperbaiki)
        // ============================================================
        navigation(
            startDestination = "asislearn_main", // Rute start yang benar
            route = "asislearn"                  // Rute graph yang benar
        ) {

            // List materi
            composable("asislearn_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                AsisLearnScreen(navController = navController, viewModel = vm)
            }

            // Upload materi
            composable("uploadMaterial") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                UploadMaterialScreen(navController = navController, viewModel = vm)
            }

            // Lihat materi
            composable(
                route = "materialDetail/{materialTitle}",
                arguments = listOf(navArgument("materialTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                val materialTitle = backStackEntry.arguments?.getString("materialTitle") ?: ""

                LihatScreen(
                    navController = navController,
                    viewModel = vm,
                    materialTitle = materialTitle
                )
            }

            // EDIT MATERI
            composable(
                route = "editMaterial/{materialId}", // Menggunakan materialId (yang isinya adalah Title)
                arguments = listOf(navArgument("materialId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asislearn")
                }
                val vm: AsisLearnViewModel = viewModel(parentEntry)

                val materialId = backStackEntry.arguments?.getString("materialId") ?: ""

                EditMaterialScreen(
                    navController = navController,
                    viewModel = vm,
                    materialId = materialId // Meneruskan ID ke EditMaterialScreen
                )
            }
        }

        // ============================================================
        // ASISHUB GRAPH (Shared ViewModel - Tidak Diubah)
        // ============================================================
        navigation(
            startDestination = "asishub_main",
            route = "asishub"
        ) {

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
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)

                PostDetailScreen(
                    navController = navController,
                    vm = asisHubViewModel
                )
            }
            composable("editPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("asishub") }
                val asisHubViewModel: AsisHubViewModel = viewModel(parentEntry)
                EditPostScreen(navController = navController, vm = asisHubViewModel)
            }

            composable("notif") {
                NotificationScreen(navController = navController)
            }
        }

        // ============================================================
        // PROFILE GRAPH (Nested Graph - Tidak Diubah)
        // ============================================================
        navigation(
            startDestination = "profile_main",
            route = "profile"
        ) {
            composable("profile_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("profile")
                }

                val context = LocalContext.current
                val api = RetrofitClient.getInstance(context)
                val repository = ProfileRepository(api)

                val profileViewModel: ProfileViewModel = viewModel(
                    parentEntry,
                    factory = ProfileViewModelFactory(repository)
                )

                ProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }

            composable("yourProfile") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("profile")
                }

                val context = LocalContext.current
                val api = RetrofitClient.getInstance(context)
                val repository = ProfileRepository(api)

                val profileViewModel: ProfileViewModel = viewModel(
                    parentEntry,
                    factory = ProfileViewModelFactory(repository)
                )

                YourProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
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