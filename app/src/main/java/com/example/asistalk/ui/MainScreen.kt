package com.example.asistalk.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.asistalk.R
import com.example.asistalk.ui.navigation.mainNavGraph

data class BottomNavItem(val label: String, val icon: Int, val route: String)

@Composable
fun MainScreen(token: String) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = mainNavController) }
    ) { innerPadding ->
        mainNavGraph(
            navController = mainNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.ic_home, "home"),
        BottomNavItem("AsisHub", R.drawable.ic_asishub, "asishub"),
        BottomNavItem("AsisLearn", R.drawable.ic_asislearn, "asislearn"),
        BottomNavItem("Profile", R.drawable.ic_profile, "profile")
    )

    NavigationBar(containerColor = Color(0xFF00BFA6)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1.0f,
                animationSpec = tween(durationMillis = 300),
                label = "scale"
            )

            val offsetY by animateFloatAsState(
                targetValue = if (isSelected) -10f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "offsetY"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier
                            .scale(scale)
                            .offset { IntOffset(0, offsetY.toInt()) }
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}