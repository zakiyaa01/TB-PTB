package com.example.asistalk.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asistalk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val recentPosts = listOf("Modul 3 PTB", "Cara Ngoding Cepat di Android Studio")
    val newMaterials = listOf("Modul Praktikum PTB", "Panduan GitHub")
    val profiles = listOf("Zakiya Aulia", "Suci Nurhaliza", "Muhammad Fikri")

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp) // Menggunakan padding horizontal agar padding atas/bawah tidak double
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Menambahkan spasi di atas
                Text(
                    text = "Hi, Asisten!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Selamat datang di AsisTalk 👋")
                Spacer(modifier = Modifier.height(24.dp))
            }

            //  Recent Posts
            item {
                Text(
                    text = "Recent Post",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(recentPosts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F1FB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(post, fontWeight = FontWeight.Bold)
                        Text("Lihat detail →", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            //  Materi Terbaru
            item {
                Text(
                    text = "Materi Terbaru",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(newMaterials) { material ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F2FF))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(material, fontWeight = FontWeight.Bold)
                        Text("Klik untuk unduh 📥", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            //  Profil Aslab Lain
            item {
                Text(
                    text = "Profil Aslab Lain",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(profiles) { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFD9E8FF), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(name, fontWeight = FontWeight.Medium)
                }
            }

            // Memberi spasi di bagian bawah agar tidak terlalu mepet dengan Bottom Navigation
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color(0xFF00BFA6)) {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /* TODO: Navigasi ke Home */ }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "AsisLearn") },
            label = { Text("AsisHub") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Learn */ }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "AsisHub") },
            label = { Text("AsisLearn") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Talk */ }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { /* TODO: Navigasi ke Profile */ }
        )
    }
}
