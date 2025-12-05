package com.example.asistalk.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage // <- 1. TAMBAHKAN IMPORT UNTUK COIL
import com.example.asistalk.R

// 2. UBAH FUNGSI UNTUK MENERIMA VIEWMODEL
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel // Terima ViewModel dari NavGraph
) {
    // 3. AMBIL DATA DARI VIEWMODEL
    val uiState = profileViewModel.uiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 4. TAMPILKAN FOTO PROFIL DI ATAS KARTU
            AsyncImage(
                model = uiState.profileImageUri ?: R.drawable.logo_asistalk_hijau,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp) // Ukuran bisa disesuaikan
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. GUNAKAN DATA DARI VIEWMODEL UNTUK NAMA DAN EMAIL/AKUN LAB
            ProfileHeader(
                name = uiState.fullName,
                email = uiState.labAccount
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- MENU (Tidak ada perubahan di sini) ---
            ProfileMenuItem(
                text = "Your Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    navController.navigate("yourProfile")
                }
            )
            ProfileMenuItem(
                text = "Notifications",
                icon = Icons.Default.Notifications,
                onClick = {
                    navController.navigate("notif")
                }
            )
            ProfileMenuItem(
                text = "Settings",
                icon = Icons.Default.Settings,
                onClick = {
                    navController.navigate("settings")
                }
            )
            ProfileMenuItem(
                text = "About",
                icon = Icons.Default.Info,
                onClick = {
                    navController.navigate("about")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            ProfileMenuItem(
                text = "Logout",
                icon = Icons.Default.ExitToApp,
                iconColor = Color.Red,
                textColor = Color.Red,
                onClick = {
                    // Logika logout
                }
            )
        }
    }
}

// 6. UBAH COMPOSABLE INI AGAR FOKUS PADA TAMPILAN NAMA & EMAIL
@Composable
private fun ProfileHeader(name: String, email: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Tampilkan nama dari ViewModel
        Text(
            text = name.ifEmpty { "User Name" }, // Beri nilai default jika kosong
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Tampilkan akun lab dari ViewModel
        Text(
            text = email.ifEmpty { "user@asistlab.ac.id" }, // Beri nilai default
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

// Composable ini tidak perlu diubah
@Composable
private fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    iconColor: Color = LocalContentColor.current,
    textColor: Color = LocalContentColor.current
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(28.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
        Divider()
    }
}

// 7. PERBAIKI PREVIEW AGAR TIDAK ERROR
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    // Karena ProfileScreen sekarang butuh ViewModel, kita buat instance palsu untuk preview
    val fakeViewModel: ProfileViewModel = viewModel()
    ProfileScreen(
        navController = rememberNavController(),
        profileViewModel = fakeViewModel
    )
}

