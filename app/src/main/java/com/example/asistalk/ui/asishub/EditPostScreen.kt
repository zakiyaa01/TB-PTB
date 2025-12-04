package com.example.asistalk.ui.asishub

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asistalk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    navController: NavHostController,
    vm: AsisHubViewModel
) {
    // Ambil data post yang sedang diedit dari ViewModel
    val postToEdit by vm.postToEdit.collectAsState()
    val selectedImageUri by vm.selectedImageUri.collectAsState()

    // State untuk menampung teks yang diedit.
    // `LaunchedEffect` digunakan untuk mengisi state ini saat layar pertama kali dibuka.
    var postContent by remember { mutableStateOf("") }

    LaunchedEffect(postToEdit) {
        postToEdit?.let {
            postContent = it.content
        }
    }

    // Launcher untuk memilih gambar dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.onImageSelected(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Postingan") },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.clearEditingState() // Bersihkan state sebelum kembali
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            // Panggil fungsi update di ViewModel
                            vm.updatePost(
                                updatedContent = postContent,
                                newImageUri = selectedImageUri
                            )
                            // Kembali ke AsisHubScreen
                            navController.popBackStack()
                        },
                        // Tombol hanya aktif jika post yang diedit ada
                        enabled = postToEdit != null
                    ) {
                        Text("Simpan")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header Info Pengguna (mirip PostCard)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A57E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = postToEdit?.author?.firstOrNull()?.toString() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(postToEdit?.author ?: "User", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Field untuk mengedit konten
            TextField(
                value = postContent,
                onValueChange = { postContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Memenuhi sisa ruang
                placeholder = { Text("Apa yang ingin anda diskusikan?") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Pratinjau Gambar yang Terpilih
            selectedImageUri?.let { uri ->
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Gambar pratinjau",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // Tombol 'X' untuk menghapus gambar
                    IconButton(
                        onClick = { vm.clearSelectedImage() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, "Hapus gambar", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Aksi Tambah Gambar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tambahkan ke postingan Anda")
                Icon(
                    painter = painterResource(R.drawable.ic_gallery),
                    contentDescription = "Tambah Gambar",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { galleryLauncher.launch("image/*") },

                    tint = LocalContentColor.current // Memberi warna default yang sesuai tema
                )
            }
        }
    }
}
