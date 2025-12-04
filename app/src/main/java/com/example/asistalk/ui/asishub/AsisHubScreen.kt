package com.example.asistalk.ui.asishub

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asistalk.R
import com.example.asistalk.ui.asishub.components.DeleteConfirmationDialog
import com.example.asistalk.utils.ComposeFileProvider

// =============================================================
// MAIN SCREEN – ASISHUB
// =============================================================
@Composable
fun AsisHubScreen(
    navController: NavHostController,
    vm: AsisHubViewModel
) {
    val posts by vm.posts.collectAsState()
    var postToDelete by remember { mutableStateOf<Post?>(null) }

    postToDelete?.let { post ->
        DeleteConfirmationDialog(
            onConfirm = {
                vm.deletePost(post.id)
                postToDelete = null
            },
            onDismiss = {
                postToDelete = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER ---
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_asistalk_hijau),
                    contentDescription = "Logo",
                    modifier = Modifier.size(65.dp)
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
                        .clickable { navController.navigate("notif") }
                )
            }
        }

        // --- INPUT BOX (SEKARANG DENGAN LOGIKA BARU) ---
        item {
            PostInputBox(
                onClickText = {
                    // Saat hanya klik teks, pastikan tidak ada gambar yang terbawa
                    vm.clearSelectedImage()
                    navController.navigate("createPost")
                },
                onNavigateToCreatePost = {
                    // Saat dari kamera, langsung navigasi (gambar sudah di ViewModel)
                    navController.navigate("createPost")
                },
                vm = vm // Teruskan ViewModel ke PostInputBox
            )
        }

        // --- POST LIST ---
        items(posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onClickPost = { navController.navigate("postDetail") },
                onClickEdit = { navController.navigate("editPost") },
                onClickDelete = {
                    postToDelete = post
                }
            )
        }
    }
}


// =============================================================
// INPUT POST BOX (DENGAN LOGIKA KAMERA)
// =============================================================
@Composable
fun PostInputBox(
    onClickText: () -> Unit,
    onNavigateToCreatePost: () -> Unit,
    vm: AsisHubViewModel // Terima ViewModel
) {
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk membuka kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let {
                    vm.onImageSelected(it) // Simpan URI ke ViewModel
                    onNavigateToCreatePost() // Pindah ke CreatePostScreen
                }
            }
        }
    )

    // Launcher untuk meminta izin kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Lakukan null-check sebelum memanggil launch
            tempImageUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
        // Jika tidak, tidak terjadi apa-apa (Anda bisa menambahkan pesan)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFDBF3FF), RoundedCornerShape(20.dp))
            .padding(14.dp),
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
        // Bagian teks yang bisa diklik
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .background(Color(0xFFEAEAEA), RoundedCornerShape(20.dp))
                .clickable { onClickText() } // Gunakan callback onClickText
                .padding(start = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Apa yang ingin anda diskusikan?", color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(10.dp))
        // Ikon kamera yang bisa diklik
        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = "Camera",
            tint = Color(0xFF00BFA5),
            modifier = Modifier.clickable { // Buat ikon bisa diklik
                // 1. Buat file & URI untuk menyimpan gambar
                val uri = ComposeFileProvider.getImageUri(context)
                tempImageUri = uri
                // 2. Minta izin kamera
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }
}

// =============================================================
// POST CARD (MENAMPILKAN GAMBAR JIKA ADA)
// =============================================================
@Composable
fun PostCard(
    post: Post,
    onClickPost: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit
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
            // ... (Bagian header PostCard tidak berubah)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A57E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.author.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(post.author, fontWeight = FontWeight.Bold)
                    Text(post.timestamp, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                DropdownMenuButton(
                    onEdit = onClickEdit,
                    onDelete = onClickDelete
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Tampilkan teks konten jika ada
            if (post.content.isNotBlank()) {
                Text(post.content, fontSize = 14.sp)
            }

            // Tampilkan gambar jika ada URI-nya
            post.imageUri?.let { uri ->
                Spacer(modifier = Modifier.height(14.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Gambar Postingan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // ... (Bagian footer/komentar tidak berubah)
            Spacer(modifier = Modifier.height(10.dp))
            Divider(thickness = 1.dp, color = Color(0xFFEAEAEA))
            Text(
                "${post.commentsCount} Comments",
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
// DROP DOWN MENU (Tidak ada perubahan)
// =============================================================
@Composable
fun DropdownMenuButton(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // ... Kode DropdownMenuButton Anda tidak perlu diubah
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

