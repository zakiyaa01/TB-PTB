package com.example.asistalk.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.R
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.ui.asislearn.MaterialItem

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AsisLearnViewModel
) {
    val userName = "Suci Nurhaliza"
    val materials by viewModel.materials.collectAsState()
    val latestMaterials = materials.take(2)

    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si? yang mau belajar takel buat besok, discord yuk!", R.drawable.ic_image, R.drawable.ic_image),
        PostData("Ria Puspita", "23115212030", "Grup belajar Algoritma dan Struktur Data sudah dibuat, cek link di bio!", R.drawable.ic_image, R.drawable.ic_image)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            HeaderWithLogo(navController)
            Spacer(Modifier.height(12.dp))
        }

        item {
            WelcomeSection(userName, navController)
            Spacer(Modifier.height(28.dp))
        }

        item {
            SectionHeaderWithAction(
                title = "Materi Terbaru",
                actionText = "Lihat Semua",
                onActionClick = { navController.navigate("asislearn") }
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                latestMaterials.forEach { material ->
                    NewMaterialCard(
                        data = material,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.getMaterialByTitle(material.title)
                            val encoded = java.net.URLEncoder.encode(material.title, "UTF-8")
                            navController.navigate("materialDetail/$encoded")
                        }
                    )
                }
                if (latestMaterials.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(28.dp))
        }

        item {
            SectionHeaderWithAction(
                title = "Diskusi Terbaru",
                actionText = "Ke AsisHub",
                onActionClick = { navController.navigate("asishub") }
            )
            Spacer(Modifier.height(12.dp))
        }

        items(recentPosts) { post ->
            RecentPostCard(post) { navController.navigate("asishub") }
            Spacer(Modifier.height(14.dp))
        }
    }
}

// ----------------------------------------------------------------------
// COMPONENTS
// ----------------------------------------------------------------------

data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val profileImageRes: Int,
    val attachedImageRes: Int
)

@Composable
fun HeaderWithLogo(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo_asistalk_hijau),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "AsisTalk",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { navController.navigate("notif") }) {
            Icon(Icons.Default.NotificationsNone, null)
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onActionClick) {
            Text(actionText, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun WelcomeSection(userName: String, navController: NavController) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Selamat datang", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { navController.navigate("asishub") }
                ) { Text("Mulai Diskusi") }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { navController.navigate("asislearn") }
                ) { Text("Lihat Materi") }
            }
        }
    }
}

@Composable
fun NewMaterialCard(data: MaterialItem, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(data.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
            Column {
                Text(
                    data.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    data.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                )
            }
        }
    }
}

@Composable
fun RecentPostCard(data: PostData, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(data.profileImageRes),
                    contentDescription = null,
                    modifier = Modifier.size(46.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(data.author, fontWeight = FontWeight.Bold)
                    Text(data.nim, style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Image(
                painter = painterResource(data.attachedImageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(data.content, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}