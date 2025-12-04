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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asistalk.R // Pastikan semua resource (R.drawable.ic_pdf, R.drawable.ic_image) ada

@Composable
fun HomeScreen(navController: NavController) {

    // --- Data Dummy Sesuai Mockup ---
    val userName = "Suci Nurhaliza"

    // Data New Material (menggambarkan materi dari AsisLearn)
    val newMaterials = listOf(
        MaterialCardData("Modul 5 Praktikum PTB", "Alexander", R.drawable.ic_pdf),
        MaterialCardData("Modul 7 Praktikum PTB", "Alexander", R.drawable.ic_image),
    )

    // Data Recent Post (menggambarkan postingan dari AsisHub)
    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si? yang mau belajar takel buat besok, discord yuk!", R.drawable.ic_image, R.drawable.ic_image),
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si? yang mau belajar takel buat besok, discord yuk!", R.drawable.ic_image, R.drawable.ic_image),
    )
    // --- Akhir Data Dummy ---

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
                newMaterials.forEach { material ->
                    NewMaterialCard(
                        data = material,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("asisLearn") } // Navigasi ke AsisLearn
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
                onClick = { navController.navigate("asisHub") } // Navigasi ke AsisHub
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Padding bawah agar konten tidak tertutup Bottom Bar
        }
    }
}

// --- Helper Data Classes ---

data class MaterialCardData(
    val title: String,
    val author: String,
    val iconRes: Int // Resource ID untuk ikon (PDF/Image)
)

data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val profileImageRes: Int,
    val attachedImageRes: Int // Untuk gambar postingan (UTS MPSI)
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
    // Container ungu sesuai mockup
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer) // Warna ungu/gelap tema
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Column {
            Text(
                text = "Wellcome!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer // Warna teks terang
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Mulai Diskusi -> AsisHubScreen
                Button(
                    onClick = { navController.navigate("asisHub") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Mulai Diskusi", color = MaterialTheme.colorScheme.onSecondary)
                }
                // Tombol Lihat Materi -> AsisLearnScreen
                Button(
                    onClick = { navController.navigate("asisLearn") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Lihat Materi", color = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }
    }
}

@Composable
fun NewMaterialCard(data: MaterialCardData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp), // Ketinggian tetap
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF13AF82) // Hijau terang sesuai mockup
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Bottom // Taruh konten di bawah
        ) {
            // Icon Placeholder (diganti dengan icon yang sesuai)
            Icon(
                painter = painterResource(id = data.iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                data.title,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                data.author,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Latar belakang putih tema
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
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        data.author,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        data.nim,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
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
            Text(data.content)

            Spacer(Modifier.height(8.dp))

            // Ikon Interaksi (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Di sini Anda bisa menambahkan IconButton untuk Like, Comment, Share
                Icon(painter = painterResource(id = R.drawable.ic_image), contentDescription = "Like", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Icon(painter = painterResource(id = R.drawable.ic_image), contentDescription = "Comment", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Icon(painter = painterResource(id = R.drawable.ic_image), contentDescription = "Share", modifier = Modifier.size(20.dp))
            }
        }
    }
}