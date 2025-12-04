package com.example.asistalk.ui.asishub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PostDetailScreen(navController: NavHostController,
                     vm: AsisHubViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Di sini Anda bisa menampilkan ulang PostCard asli
        Text("Detail Postingan", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Di bawahnya adalah daftar komentar
        Text("Komentar:", fontWeight = FontWeight.SemiBold)
        // ... Logika untuk menampilkan daftar komentar ...
    }
}