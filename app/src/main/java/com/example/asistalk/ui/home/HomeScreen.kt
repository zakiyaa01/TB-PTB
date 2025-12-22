package com.example.asistalk.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.network.MaterialItem // Menggunakan model resmi dari network
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary

// HAPUS data class MaterialItem lama di sini agar tidak redeclaration!

data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val profileImageRes: Int,
    val attachedImageRes: Int
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AsisLearnViewModel // TAMBAHKAN INI agar NavGraph tidak error
) {
    // Ambil data dari API saat Home dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchAllMaterials()
    }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userName = viewModel.currentUserFullName // Ambil dari ViewModel

    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget!", 0, 0),
        PostData("Ria Puspita", "23115212030", "Grup belajar ASD sudah dibuat", 0, 0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { HomeHeader() }
        item { FloatingWelcomeCard(userName, navController) }

        item {
            SectionHeaderWithAction("Materi Terbaru", "Lihat Semua") {
                navController.navigate("asislearn")
            }
        }

        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            // Tampilkan 3 materi terbaru dari API
            items(materials.take(3)) { material ->
                NewMaterialCard(material) {
                    // Navigasi ke detail atau list
                    navController.navigate("asislearn")
                }
            }
        }

        item { SectionHeaderWithAction("Diskusi Terbaru", "Ke AsisHub") {
            navController.navigate("asishub")
        } }

        items(recentPosts) { post ->
            RecentPostCard(post) {}
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = Secondary,
                shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
            )
    )
}

@Composable
fun FloatingWelcomeCard(userName: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-50).dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Selamat Datang👋")
            Text(userName, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(modifier = Modifier.weight(1f), onClick = { navController.navigate("asishub") }) { Text("Diskusi") }
                Button(modifier = Modifier.weight(1f), onClick = { navController.navigate("asislearn") }) { Text("Materi") }
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onActionClick) { Text(actionText) }
    }
}

@Composable
fun NewMaterialCard(data: MaterialItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(data.subject, fontWeight = FontWeight.Bold) // Sesuaikan: title jadi subject
            Text(data.topic)
            Text("By: ${data.author_name}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun RecentPostCard(data: PostData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(data.author, fontWeight = FontWeight.Bold)
            Text(data.content)
        }
    }
}