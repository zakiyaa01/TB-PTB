package com.example.asistalk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.asistalk.ui.auth.LoginScreen
import com.example.asistalk.ui.auth.RegisterScreen
import com.example.asistalk.ui.home.HomeScreen
import com.example.asistalk.ui.asishub.AsisHubScreen
import com.example.asistalk.ui.asislearn.AsisLearnScreen
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
        mainNavGraph()
    }
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "login",
        route = Graph.AUTHENTICATION
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.mainNavGraph() {
    navigation(
        startDestination = "home",
        route = Graph.MAIN
    ) {
        composable("home") { HomeScreen() }
        composable("asishub") { AsisHubScreen() }
        composable("asislearn") { AsisLearnScreen() }
        composable("profile") { ProfileScreen() }
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}
