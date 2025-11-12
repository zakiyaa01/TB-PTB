package com.example.asistalk.ui.profile

import androidx.compose.foundation.layout.* // ✅ biar Modifier, Box, fillMaxSize() dikenali
import androidx.compose.material3.* // ✅ biar Text, MaterialTheme dikenali
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
