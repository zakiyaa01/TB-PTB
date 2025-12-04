package com.example.asistalk.ui.asishub

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asistalk.utils.ComposeFileProvider

@Composable
fun CreatePostScreen(
    navController: NavHostController,
    vm: AsisHubViewModel
) {
    var postContent by remember { mutableStateOf("") }
    val context = LocalContext.current

    val imageUriFromVM by vm.selectedImageUri.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(imageUriFromVM) {
        imageUri = imageUriFromVM
    }

    // --- PERUBAHAN 1: Menggabungkan Launcher Galeri ---
    // Launcher untuk memilih MEDIA (Gambar DAN Video) dari galeri
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Saat ini kita anggap semua yang dipilih adalah gambar
                // TODO: Di masa depan, Anda bisa menambahkan logika untuk membedakan URI video
                imageUri = it // Update UI
                vm.onImageSelected(it) // Simpan ke ViewModel
            }
        }
    )

    // Launcher untuk kamera tidak berubah
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) { /* URI sudah di-update sebelum launch, UI akan otomatis refresh */ }
        }
    )

    // Launcher untuk izin kamera tidak berubah
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri = uri
                vm.onImageSelected(uri)
                takePhotoLauncher.launch(uri)
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            vm.clearSelectedImage()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Buat Postingan Baru", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Pratinjau gambar (tidak berubah)
        imageUri?.let { uri ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Pratinjau Gambar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = postContent,
            onValueChange = { postContent = it },
            placeholder = { Text("Apa yang ingin anda diskusikan?") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )

        Spacer(Modifier.height(16.dp))

        // --- PERUBAHAN 2: Tombol-tombol Aksi yang Sudah Digabung ---
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Tombol 1: Buka Galeri (untuk Foto & Video)
            Button(onClick = {
                // Membuka galeri yang menampilkan gambar DAN video
                pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeri")
                    Spacer(Modifier.width(8.dp))
                    Text("Galeri")
                }
            }

            // Tombol 2: Ambil Foto Baru dari Kamera
            Button(onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Kamera")
                    Spacer(Modifier.width(8.dp))
                    Text("Kamera")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Tombol Posting (tidak berubah)
        Button(
            onClick = {
                if (postContent.isNotBlank() || imageUri != null) {
                    vm.addPost(
                        author = "Zakiya Aulia",
                        content = postContent,
                        imageUri = imageUri
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Posting")
        }
    }
}