package com.example.asistalk.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asistalk.R
import com.example.asistalk.ui.asislearn.AsisLearnViewModel // Import ViewModel
import com.example.asistalk.ui.asislearn.MaterialItem // Import Data Class

// PENTING: HomeScreen harus menerima ViewModel
@Composable
fun HomeScreen(
    navController: NavController,
    // Asumsi ViewModel disuntikkan dari NavGraph (sama seperti AsisLearnScreen)
    viewModel: AsisLearnViewModel
) {

    // --- Data Dinamis dari ViewModel ---
    val userName = "Suci Nurhaliza" // Tetap dummy atau ambil dari User ViewModel

    // Ambil 2 materi terbaru dari daftar materi yang diupload (sudah diurutkan berdasarkan waktu upload)
    val newMaterials by viewModel.materials.collectAsState()
    val latestMaterials = newMaterials.take(2) // Ambil 2 yang terbaru

    // Data Recent Post (tetap dummy karena belum ada AsisHubViewModel)
    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si? yang mau belajar takel buat besok, discord yuk!", R.drawable.ic_image, R.drawable.ic_image),
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si? yang mau belajar takel buat besok, discord yuk!", R.drawable.ic_image, R.drawable.ic_image),
    )
    // --- Akhir Data Dinamis ---

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. Header Welcome ---
        item {
            WelcomeHeader(userName = userName, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 2. New Material ---
        item {
            SectionTitle("New Material")
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Perbaikan: Iterasi menggunakan data dari ViewModel
                latestMaterials.forEach { material ->
                    NewMaterialCard(
                        data = material, // Meneruskan MaterialItem
                        modifier = Modifier.weight(1f),
                        // Navigasi ke detail materi (LihatScreen)
                        onClick = {
                            // Lakukan seperti di AsisLearnScreen
                            viewModel.getMaterialByTitle(material.title)
                            val encodedTitle = java.net.URLEncoder.encode(material.title, "UTF-8")
                            navController.navigate("materialDetail/$encodedTitle")
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        // --- 3. Recent Post ---
        item {
            SectionTitle("Recent Post")
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(recentPosts) { post ->
            RecentPostCard(
                data = post,
                onClick = { navController.navigate("asishub") } // Navigasi ke AsisHub (ganti rute jika perlu)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Hapus MaterialCardData karena menggunakan MaterialItem dari ViewModel ---

data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val profileImageRes: Int,
    val attachedImageRes: Int
)

// --- Helper Composables ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun WelcomeHeader(userName: String, navController: NavController) {
    // Container header
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            // PERBAIKAN 1: BACKGROUND DIUBAH MENJADI SECONDARY
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Column {
            Text(
                text = "Wellcome!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                // Menggunakan onSecondary karena latar belakangnya Secondary
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Mulai Diskusi -> AsisHubScreen
                Button(
                    onClick = { navController.navigate("asishub") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        // PERBAIKAN 2: CONTAINER DIUBAH MENJADI PRIMARY
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Mulai Diskusi", color = MaterialTheme.colorScheme.onPrimary)
                }
                // Tombol Lihat Materi -> AsisLearnScreen
                Button(
                    onClick = { navController.navigate("asislearn") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        // PERBAIKAN 3: CONTAINER DIUBAH MENJADI PRIMARY
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Lihat Materi", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun NewMaterialCard(
    data: MaterialItem, // Menerima MaterialItem dari ViewModel
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Perbaikan Warna: Menggunakan Secondary (Hijau yang Anda definisikan: 0xFF00D1B2)
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Icon Placeholder
            Icon(
                painter = painterResource(id = data.icon), // Menggunakan icon dari MaterialItem
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                data.title, // Judul dinamis
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                data.author, // Pengarang dinamis
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RecentPostCard(data: PostData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Baris Profil
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = data.profileImageRes),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        // Menggunakan PrimaryLight (D2EEF7) sebagai latar belakang gambar profil
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        data.author,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground // DarkText
                    )
                    Text(
                        data.nim,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // GrayText
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Gambar Postingan (Attachment)
            Image(
                painter = painterResource(id = data.attachedImageRes),
                contentDescription = "Post Attachment",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                alignment = Alignment.TopCenter
            )

            Spacer(Modifier.height(8.dp))

            // Konten Post
            Text(data.content, color = MaterialTheme.colorScheme.onBackground)

            Spacer(Modifier.height(8.dp))

            // Ikon Interaksi (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Ikon menggunakan warna tema untuk konsistensi
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Like",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Comment",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}