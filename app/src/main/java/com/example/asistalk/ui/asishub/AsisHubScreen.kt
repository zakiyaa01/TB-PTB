package com.example.asistalk.ui.asishub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asistalk.R
import com.example.asistalk.ui.asishub.components.DeleteConfirmationDialog

// =============================================================
// MAIN SCREEN – ASISHUB
// =============================================================
@Composable
fun AsisHubScreen(navController: NavHostController) {

    // --- PERUBAHAN 1: State untuk mengontrol dialog ---
    // Menyimpan ID atau informasi postingan yang akan dihapus
    var postToDelete by remember { mutableStateOf<String?>(null) }

    // Tampilkan dialog jika postToDelete tidak null
    if (postToDelete != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                // Di sini Anda akan menambahkan logika untuk benar-benar menghapus postingan
                // Misalnya: viewModel.deletePost(postToDelete)
                println("Postingan ${postToDelete} dihapus!") // Placeholder
                postToDelete = null // Tutup dialog setelah konfirmasi
            },
            onDismiss = {
                postToDelete = null // Tutup dialog jika dibatalkan
            }
        )
    }

    // Menggunakan LazyColumn agar bisa di-scroll jika postingan banyak
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // --- HEADER ---
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp) // Beri padding vertikal
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "AsisHub",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2098D1)
                )
                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = "Notif",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            navController.navigate("notif")
                        }
                )
            }
        }

        // --- INPUT BOX ---
        item {
            PostInputBox(onClick = {
                navController.navigate("createPost")
            })
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- POST LIST ---
        item {
            PostCard(
                // --- PERUBAHAN 2: Memberi ID unik untuk setiap post ---
                postId = "post_1",
                name = "Zakiya Aulia",
                time = "2 hours ago",
                content = "Tutorial lengkap instalasi Ubuntu Server…",
                comments = 2,
                hasMedia = true,
                onClickPost = { navController.navigate("postDetail") },
                onClickEdit = { navController.navigate("editPost") },
                onClickDelete = { postId ->
                    // Set state dengan ID postingan yang ingin dihapus
                    postToDelete = postId
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            PostCard(
                postId = "post_2",
                name = "Suci Nurhaliza",
                time = "5 hours ago",
                content = "Halo semuanya! Ada yang tau cara mengatasi error…",
                comments = 1,
                hasMedia = false,
                onClickPost = { navController.navigate("postDetail") },
                onClickEdit = { navController.navigate("editPost") },
                onClickDelete = { postId ->
                    postToDelete = postId
                }
            )
        }
    }
}


// =============================================================
// INPUT POST BOX
// =============================================================
@Composable
fun PostInputBox(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color(0xFFDBF3FF), RoundedCornerShape(20.dp))
            .padding(14.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A57E8)),
            contentAlignment = Alignment.Center
        ) {
            Text("Z", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .background(Color(0xFFEAEAEA), RoundedCornerShape(20.dp))
                .padding(start = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Apa yang ingin anda diskusikan?", color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = "Camera",
            tint = Color(0xFF00BFA5)
        )
    }
}

// =============================================================
// POST CARD
// =============================================================
@Composable
fun PostCard(
    postId: String, // <-- Parameter baru untuk identifikasi
    name: String,
    time: String,
    content: String,
    comments: Int,
    hasMedia: Boolean,
    onClickPost: () -> Unit,
    onClickEdit: () -> Unit,
    // --- PERUBAHAN 3: onClickDelete sekarang memberikan ID kembali ---
    onClickDelete: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickPost() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A57E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(time, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.weight(1f))

                DropdownMenuButton(
                    onEdit = onClickEdit,
                    // Memanggil onClickDelete dengan postId saat tombol di menu diklik
                    onDelete = { onClickDelete(postId) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(content, fontSize = 14.sp)

            if (hasMedia) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFFE5E5E5), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_video),
                        contentDescription = "Video",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(thickness = 1.dp, color = Color(0xFFEAEAEA))

            Text(
                "$comments Comments",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}

// =============================================================
// DROP DOWN MENU
// =============================================================
@Composable
fun DropdownMenuButton(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "Menu",
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Post") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Post") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}