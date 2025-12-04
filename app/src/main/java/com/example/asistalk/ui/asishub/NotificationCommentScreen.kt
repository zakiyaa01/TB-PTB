package com.example.asistalk.ui.asishub

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun NotificationScreen(navController: NavHostController) {
    Column(Modifier.padding(16.dp)) {
        Text("Notifikasi Komentar", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        NotificationItem("Zakiya Aulia berkomentar di postingan Anda")
        NotificationItem("Suci Nurhaliza membalas komentar Anda")
        NotificationItem("Ahmad Fauzi membalas komentar Anda")
    }
}

@Composable
fun NotificationItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5FF))
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            fontSize = 15.sp
        )
    }
}