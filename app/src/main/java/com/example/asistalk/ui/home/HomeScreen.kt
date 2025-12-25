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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.asistalk.R
import com.example.asistalk.network.MaterialItem
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.ui.asishub.AsisHubViewModel
import com.example.asistalk.ui.asishub.Post
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary
import com.example.asistalk.utils.UserPreferencesRepository
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(
    navController: NavController,
    asisLearnVm: AsisLearnViewModel, // Untuk fitur materi (Suci)
    asisHubVm: AsisHubViewModel      // Untuk fitur diskusi (Zakiya)
) {
    val context = LocalContext.current
    val userPrefsRepo = remember { UserPreferencesRepository(context) }

    // State dari AsisLearn (Materi)
    val materials by asisLearnVm.materials.collectAsState()
    val isLearnLoading by asisLearnVm.isLoading.collectAsState()

    // State dari AsisHub (Diskusi)
    val posts by asisHubVm.posts.collectAsState()

    val fullName = asisLearnVm.currentUserFullName
    val displayName = remember(fullName) {
        if (fullName.isNotBlank()) fullName.split(" ").first() else "Mahasiswa"
    }

    LaunchedEffect(Unit) {
        val id = userPrefsRepo.userIdFlow.first()
        val nameFromPrefs = userPrefsRepo.fullnameFlow.first()

        asisLearnVm.setSession(id, nameFromPrefs)
        asisLearnVm.fetchAllMaterials()
        asisHubVm.fetchPosts()
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
            // 1. HEADER & WELCOME CARD
            item { HeaderSection() }
            item {
                WelcomeCard(
                    displayName = displayName,
                    onDiskusiClick = { navController.navigate("asishub") },
                    onMateriClick = { navController.navigate("asislearn") }
                )
            }

            // 2. MATERI TERBARU (Data dari Suci)
            item {
                SectionHeader(
                    title = "Materi Terbaru",
                    onActionClick = { navController.navigate("asislearn") }
                )
            }

            if (isLearnLoading) {
                item { LoadingIndicator() }
            } else {
                items(materials.take(2)) { material ->
                    NewMaterialCard(
                        data = material,
                        onClick = { navController.navigate("detailMaterial/${material.id}") }
                    )
                }
            }

            // 3. POSTINGAN TERBARU (Data dari Zakiya)
            item {
                Spacer(Modifier.height(16.dp))
                SectionHeader(
                    title = "Postingan Terbaru",
                    onActionClick = { navController.navigate("asishub") }
                )
            }

            // Cukup gunakan pengecekan isEmpty() agar tidak error mencari isHubLoading
            if (posts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada postingan", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                items(posts.take(2), key = { it.id }) { post ->
                    RecentPostCard(
                        post = post,
                        onClick = {
                            asisHubVm.selectPostForViewing(post)
                            navController.navigate("postDetail")
                        }
                    )
                }
            }
        }
    }
}

// --- KOMPONEN UI ---

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
            Text("Selamat Datang di AsisTalk", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        }
    }
}

@Composable
fun WelcomeCard(displayName: String, onDiskusiClick: () -> Unit, onMateriClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-50).dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { // Perbaikan Alignment
                Box(Modifier.size(45.dp).background(Primary.copy(alpha = 0.1f), CircleShape), Alignment.Center) {
                    Icon(Icons.Default.WavingHand, null, tint = Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Halo, $displayName!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Mau belajar apa hari ini?", fontSize = 13.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Perbaikan Arrangement
            ) {
                ActionHomeButton("Diskusi", Icons.Default.ChatBubbleOutline, Modifier.weight(1f), onDiskusiClick)
                ActionHomeButton("Materi", Icons.Default.MenuBook, Modifier.weight(1f), onMateriClick)
            }
        }
    }
}

@Composable
fun RecentPostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = if (!post.authorProfileImage.isNullOrEmpty()) "http://10.0.2.2:3000${post.authorProfileImage}" else R.drawable.logo_asistalk_hijau,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFE2E8F0)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(post.timestamp, fontSize = 11.sp, color = Color.LightGray)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = Color(0xFF475569), maxLines = 2)
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
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center, // Perbaikan Arrangement
            verticalAlignment = Alignment.CenterVertically  // Perbaikan Alignment
        ) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Bold, color = Primary)
        }
    }
}

@Composable
fun SectionHeader(title: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically // Perbaikan Alignment
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onActionClick) {
            Text("Lihat Semua", color = Primary)
            Icon(Icons.Default.ChevronRight, null, tint = Primary)
        }
    }
}

@Composable
fun NewMaterialCard(data: MaterialItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Perbaikan Alignment
        ) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Secondary.copy(0.1f)), Alignment.Center) {
                Icon(Icons.Default.Description, null, tint = Secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(data.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(data.topic, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}