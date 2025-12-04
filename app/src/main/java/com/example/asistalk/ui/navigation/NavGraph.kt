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
                onNavigateBackToLogin = { navController.popBackStack() }
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

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // ============================================================
        // ASISLEARN GRAPH (Shared ViewModel)
        // ============================================================
        navigation(
            startDestination = "asislearn_main",
            route = "asislearn"
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

            // Detail materi
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
        }

        // ============================================================
        // ASISHUB GRAPH (Shared ViewModel)
        // ============================================================
        navigation(
            startDestination = "asishub_main",
            route = "asishub"
        ) {

            composable("asishub_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val vm: AsisHubViewModel = viewModel(parentEntry)
                AsisHubScreen(navController = navController, vm = vm)
            }

            composable("createPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val vm: AsisHubViewModel = viewModel(parentEntry)
                CreatePostScreen(navController = navController, vm = vm)
            }

            composable("postDetail") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val vm: AsisHubViewModel = viewModel(parentEntry)
                PostDetailScreen(navController = navController, vm = vm)
            }

            composable("editPost") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("asishub")
                }
                val vm: AsisHubViewModel = viewModel(parentEntry)
                EditPostScreen(navController = navController, vm = vm)
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