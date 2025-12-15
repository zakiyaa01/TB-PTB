// HomeScreen dengan Header Melengkung & UI Modern
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.R
import com.example.asistalk.ui.asislearn.AsisLearnViewModel
import com.example.asistalk.ui.asislearn.MaterialItem
import androidx.compose.ui.graphics.Brush
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AsisLearnViewModel
) {
    val userName = "Suci Nurhaliza"
    val materials by viewModel.materials.collectAsState()
    val latestMaterials = materials.take(2)

    val recentPosts = listOf(
        PostData("Zakiya Aulia", "23115212029", "UTS MPSI tadi susah banget ga si?", R.drawable.ic_image, R.drawable.ic_image),
        PostData("Ria Puspita", "23115212030", "Grup belajar ASD sudah dibuat", R.drawable.ic_image, R.drawable.ic_image)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // HEADER + WELCOME CARD
        item {
            Box {
                HomeHeader()
                HeaderWithLogo(navController)
                FloatingWelcomeCard(
                    userName = userName,
                    navController = navController,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = 130.dp)
                )
            }
            Spacer(Modifier.height(110.dp))
        }

        // MATERI TERBARU
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

        // DISKUSI TERBARU
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

// ================== COMPONENT ==================

data class PostData(
    val author: String,
    val nim: String,
    val content: String,
    val profileImageRes: Int,
    val attachedImageRes: Int
)

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Secondary,
                        Primary
                    )
                ),
                shape = RoundedCornerShape(
                    bottomStart = 60.dp,
                    bottomEnd = 60.dp
                )
            )
    )
}

@Composable
fun HeaderWithLogo(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo_asistalk_putih),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "AsisTalk",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { navController.navigate("notif") }) {
            Icon(Icons.Default.NotificationsNone, null, tint = Color.White)
        }
    }
}

@Composable
fun FloatingWelcomeCard(
    userName: String,
    navController: NavController,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Selamat Datang👋", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { navController.navigate("asishub") }
                ) { Text("Diskusi") }

                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { navController.navigate("asislearn") }
                ) { Text("Materi") }
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onActionClick) {
            Text(actionText)
            Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun NewMaterialCard(data: MaterialItem, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(data.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(data.title, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(
                    data.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    modifier = Modifier.size(44.dp).clip(CircleShape)
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
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(data.content, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}