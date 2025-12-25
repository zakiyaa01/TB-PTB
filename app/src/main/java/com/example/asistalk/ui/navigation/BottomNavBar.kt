package com.example.asistalk.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.asistalk.R

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Home", R.drawable.ic_home, "home"),
        NavItem("AsisHub", R.drawable.ic_asishub, "asishub"),
        NavItem("AsisLearn", R.drawable.ic_asislearn, "asislearn"),
        NavItem("Profile", R.drawable.ic_profile, "profile")
    )

    NavigationBar(
        tonalElevation = 10.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

data class NavItem(val label: String, val icon: Int, val route: String)