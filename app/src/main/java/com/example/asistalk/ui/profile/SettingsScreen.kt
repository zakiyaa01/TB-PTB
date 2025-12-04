package com.example.asistalk.ui.profile

import android.widget.Toast
// import androidx.appcompat.app.AppCompatDelegate // <-- Dinonaktifkan untuk sementara
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.asistalk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // State untuk switch notifikasi
    var notificationEnabled by remember { mutableStateOf(true) }

    // --- PERBAIKAN: Logika Dark Mode dinonaktifkan sementara ---
    // State dummy untuk switch dark mode agar UI tetap tampil.
    var darkModeEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER PROFIL ---
            ProfileHeader(
                name = "Zakiya Aulia",
                email = "zakiyaa@AsistLab.ac.id"
            )

            // --- GENERAL SETTINGS ---
            SettingsSectionTitle("General")
            SettingsMenuItem(
                title = "Languages",
                subtitle = "English",
                onClick = {
                    Toast.makeText(context, "Open Languages Menu", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsMenuItem(
                title = "Font Size",
                subtitle = "Medium",
                onClick = {
                    Toast.makeText(context, "Open Font Size Menu", Toast.LENGTH_SHORT).show()
                }
            )

            // --- ACCESSIBILITY SETTINGS ---
            SettingsSectionTitle("Accessibility")
            SettingsSwitchItem(
                title = "Dark Mode",
                checked = darkModeEnabled,
                onCheckedChange = { isChecked ->
                    darkModeEnabled = isChecked
                    // --- PERBAIKAN: Panggilan AppCompatDelegate dihapus ---
                    // val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    // AppCompatDelegate.setDefaultNightMode(mode)

                    // Ganti dengan pesan sementara
                    Toast.makeText(context, "Dark mode feature is under development", Toast.LENGTH_SHORT).show()
                }
            )

            // --- NOTIFICATION SETTINGS ---
            SettingsSectionTitle("Notifications")
            SettingsSwitchItem(
                title = "Allow Notifications",
                checked = notificationEnabled,
                onCheckedChange = { isChecked ->
                    notificationEnabled = isChecked
                    val message = if (isChecked) "Notifications Enabled" else "Notifications Disabled"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

// --- Komponen-komponen UI yang bisa digunakan kembali ---

@Composable
private fun ProfileHeader(name: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_asistalk_hijau),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = email, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsMenuItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        Divider(modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
        Divider(modifier = Modifier.padding(start = 16.dp))
    }
}

@Preview(showBackground = true, name = "Settings Screen")
@Composable
fun SettingsScreenPreview() {
    // Kita bungkus dengan theme agar preview-nya akurat
    MaterialTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
