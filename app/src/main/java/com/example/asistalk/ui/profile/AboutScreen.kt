package com.example.asistalk.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.asistalk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About App") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO APLIKASI ---
            Image(
                painter = painterResource(id = R.drawable.logo_asistalk_hijau),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- NAMA APLIKASI ---
            Text(
                text = "AsisTalk App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // --- VERSI APLIKASI ---
            Text(
                text = "Version 1.0.0",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- DESKRIPSI ---
            Text(
                text = """
                    This application is developed to support the daily activities
                    of AsistLab users, including learning, sharing materials,
                    managing attendance, and communication between members.
                """.trimIndent(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Anda bisa menambahkan info lain di sini jika perlu
            // Spacer(modifier = Modifier.height(24.dp))
            // Text(text = "Developed by: Your Name", color = Color.Gray)

        }
    }
}

// Untuk melihat pratinjau di Android Studio
@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    // Kita bungkus dengan theme agar preview-nya akurat
    MaterialTheme {
        AboutScreen(navController = rememberNavController())
    }
}