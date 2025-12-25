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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.network.MaterialItem
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary
import com.example.asistalk.utils.UserPreferencesRepository
import kotlinx.coroutines.flow.first

// --- Model Data Lokal ---
data class PostData(
    val author: String,
    val content: String,
    val time: String = "Baru saja"
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AsisLearnViewModel
) {
    val context = LocalContext.current
    val userPrefsRepo = remember { UserPreferencesRepository(context) }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val fullName = viewModel.currentUserFullName
    val displayName = remember(fullName) {
        if (fullName.isNotBlank()) fullName.split(" ").first() else "Mahasiswa"
    }

    LaunchedEffect(Unit) {
        val id = userPrefsRepo.userIdFlow.first()
        val nameFromPrefs = userPrefsRepo.fullnameFlow.first()

        viewModel.setSession(id, nameFromPrefs)
        viewModel.fetchAllMaterials()
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. HEADER
            item {
                HeaderSection()
            }

            // 2. FLOATING CARD
            item {
                WelcomeCard(
                    displayName = displayName,
                    onDiskusiClick = { navController.navigate("asishub") },
                    onMateriClick = { navController.navigate("asislearn") }
                )
            }

            // 3. SECTION MATERI TERBARU
            item {
                SectionHeader(
                    title = "Materi Terbaru",
                    onActionClick = { navController.navigate("asislearn") }
                )
            }

            if (isLoading) {
                item { LoadingIndicator() }
            } else {
                items(materials.take(2)) { material ->
                    NewMaterialCard(material) {
                        navController.navigate("detailMaterial/${material.id}")
                    }
                }
            }

            // 4. DISKUSI
            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(
                    title = "Diskusi Populer",
                    onActionClick = { navController.navigate("asishub") }
                )
            }

            val dummyPosts = listOf(
                PostData("Zakiya Aulia", "UTS MPSI tadi susah banget, ada yang punya kisi-kisi?", "10m yang lalu"),
                PostData("Ria Puspita", "Grup belajar ASD sudah dibuat, cek link di bio ya guys!", "1j yang lalu")
            )

            items(dummyPosts) { post ->
                RecentPostCard(post)
            }
        }
    }
}

// --- Komponen UI Tetap Sama ---

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(colors = listOf(Primary, Secondary)),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Column {
            Text("AsisTalk", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
            Text("Selamat Datang di AsisTalk", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 34.sp)
        }
    }
}

@Composable
fun WelcomeCard(
    displayName: String,
    onDiskusiClick: () -> Unit,
    onMateriClick: () -> Unit
) {
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
                    modifier = Modifier.size(45.dp).background(Primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WavingHand, null, tint = Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Halo, $displayName!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("Mau belajar apa hari ini?", fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionHomeButton("Diskusi", Icons.Default.ChatBubbleOutline, Modifier.weight(1f), onDiskusiClick)
                ActionHomeButton("Materi", Icons.Default.MenuBook, Modifier.weight(1f), onMateriClick)
            }
        }
    }
}

@Composable
fun ActionHomeButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
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
                .background(Brush.horizontalGradient(listOf(Primary.copy(0.08f), Secondary.copy(0.02f)))),
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
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Secondary.copy(0.1f)), Alignment.Center) {
                Icon(Icons.Default.Description, null, tint = Secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(data.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF334155))
                Text(data.topic, fontSize = 13.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).background(Color(0xFFE2E8F0), CircleShape))
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
@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
        CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
    }
}