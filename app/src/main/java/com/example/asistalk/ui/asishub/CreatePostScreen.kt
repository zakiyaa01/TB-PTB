package com.example.asistalk.ui.asishub

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CreatePostScreen(navController: NavHostController) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create New Post", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "", // Nanti akan diganti dengan state
            onValueChange = {},
            placeholder = { Text("Apa yang ingin anda diskusikan?") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Beri ruang lebih untuk mengetik
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = {}) { Text("📷 Photo") }
            Spacer(Modifier.width(10.dp))
            Button(onClick = {}) { Text("🎬 Video") }
        }

        Spacer(Modifier.height(20.dp))

        // Tombol Posting dan Kembali
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Batal")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    // Logika untuk memposting
                    navController.popBackStack() // Kembali setelah posting
                }
            ) {
                Text("Posting")
            }
        }
    }
}