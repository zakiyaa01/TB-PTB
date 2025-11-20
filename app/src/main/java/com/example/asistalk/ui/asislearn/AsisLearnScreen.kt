package com.example.asistalk.ui.asislearn

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material3.Text

@Composable
fun AsisLearnScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Halaman AsisLearn", style = MaterialTheme.typography.titleLarge)
    }
}
