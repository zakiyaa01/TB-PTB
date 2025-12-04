package com.example.asistalk.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.asistalk.R

@Composable
fun ProfileScreen(navController: NavController) {
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

            // --- KARTU PROFIL PENGGUNA ---
            ProfileHeaderCard(
                name = "Zakiya Aulia",
                email = "zakiyaa@AsistLab.ac.id"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- MENU ---

            // ✅ PERBAIKI ONCLICK DI SINI
            ProfileMenuItem(
                text = "Your Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    // Panggil rute yang sudah didaftarkan di NavGraph
                    navController.navigate("yourProfile")
                }
            )

            // ✅ PERBAIKI ONCLICK DI SINI
            ProfileMenuItem(
                text = "Notifications",
                icon = Icons.Default.Notifications,
                onClick = {
                    navController.navigate("notif") // Rute ini sudah ada di dalam grup asishub
                }
            )

            // ✅ PERBAIKI ONCLICK DI SINI
            ProfileMenuItem(
                text = "Settings",
                icon = Icons.Default.Settings,
                onClick = {
                    navController.navigate("settings")
                }
            )

            // ✅ PERBAIKI ONCLICK DI SINI
            ProfileMenuItem(
                text = "About",
                icon = Icons.Default.Info,
                onClick = {
                    navController.navigate("about")
                }
            )

            // --- SPACER & LOGOUT ---
            Spacer(modifier = Modifier.weight(1f))

            ProfileMenuItem(
                text = "Logout",
                icon = Icons.Default.ExitToApp,
                iconColor = Color.Red,
                textColor = Color.Red,
                onClick = {
                    // Tampilkan dialog konfirmasi atau langsung logout
                }
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(name: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_asistalk_hijau), // Ganti dengan foto profil asli jika ada
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = email, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

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

// Untuk melihat pratinjau di Android Studio
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}
