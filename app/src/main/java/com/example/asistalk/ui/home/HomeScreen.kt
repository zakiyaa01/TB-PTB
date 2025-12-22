package com.example.asistalk.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.network.MaterialItem
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary

// Model data lokal untuk Post
data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val time: String = "Baru saja"
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AsisLearnViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.fetchAllMaterials()
    }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userName = viewModel.currentUserFullName

    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget!", "10m yang lalu"),
        PostData("Ria Puspita", "23115212030", "Grup belajar ASD sudah dibuat, cek link di bio ya.", "1j yang lalu")
    )

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // --- HEADER GRADASI ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Primary, Secondary)
                            ),
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                ) {
                    Column {
                        Text(
                            "AsisTalk",
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            "Solusi Belajar Mahasiswa",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        )
                    }
                }
            }

            // --- FLOATING CARD ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-50).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.WavingHand,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Halo, ${userName.ifEmpty { "Mahasiswa" }}!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text("Mau upgrade skill apa hari ini?", fontSize = 13.sp, color = Color.Gray)
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // --- TOMBOL MODERN (SATU WARNA + STROKE) ---
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ActionHomeButton(
                                text = "Diskusi",
                                icon = Icons.Default.ChatBubbleOutline,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("asishub") }
                            )
                            ActionHomeButton(
                                text = "Materi",
                                icon = Icons.Default.MenuBook,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("asislearn") }
                            )
                        }
                    }
                }
            }

            // --- SECTION MATERI ---
            item {
                SectionHeader(
                    title = "Materi Terbaru",
                    onActionClick = { navController.navigate("asislearn") }
                )
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
                    }
                }
            } else {
                items(materials.take(3)) { material ->
                    NewMaterialCard(material) {
                        navController.navigate("asislearn")
                    }
                }
            }

            // --- SECTION DISKUSI ---
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader(
                    title = "Diskusi Populer",
                    onActionClick = { navController.navigate("asishub") }
                )
            }

            items(recentPosts) { post ->
                RecentPostCard(post)
            }
        }
    }
}

@Composable
fun ActionHomeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.08f),
                            Secondary.copy(alpha = 0.02f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Primary)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 0.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onActionClick) {
            Text("Lihat Semua", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp), tint = Primary)
        }
    }
}

@Composable
fun NewMaterialCard(data: MaterialItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Secondary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, null, tint = Secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(data.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF334155))
                Text(data.topic, fontSize = 13.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
                    Spacer(Modifier.width(4.dp))
                    Text(data.author_name, fontSize = 11.sp, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun RecentPostCard(data: PostData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(Color(0xFFE2E8F0), CircleShape))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(data.author, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                    Text(data.time, fontSize = 11.sp, color = Color.LightGray)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(data.content, fontSize = 14.sp, color = Color(0xFF475569), lineHeight = 20.sp)
        }
    }
}