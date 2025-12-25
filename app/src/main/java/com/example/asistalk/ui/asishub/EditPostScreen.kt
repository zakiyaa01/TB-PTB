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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.asistalk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    navController: NavHostController,
    vm: AsisHubViewModel
) {
    val postToEdit by vm.postToEdit.collectAsState()
    val selectedImageUri by vm.selectedImageUri.collectAsState()

    var postContent by remember { mutableStateOf("") }

    LaunchedEffect(postToEdit) {
        postToEdit?.let {
            postContent = it.content
        }
    }

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
                        vm.clearEditingState()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            vm.updatePost(
                                updatedContent = postContent,
                                newImageUri = selectedImageUri
                            )
                            navController.popBackStack()
                        },
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "http://10.0.2.2:3000${postToEdit?.authorProfileImage}",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.logo_asistalk_hijau)
                )

                Spacer(modifier = Modifier.width(10.dp))
                Text(postToEdit?.author ?: "User", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = postContent,
                onValueChange = { postContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("Apa yang ingin anda diskusikan?") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Perbaikan Logika Preview Gambar
            val previewImage = selectedImageUri ?: postToEdit?.imageUri

            previewImage?.let { uri ->
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
                    tint = LocalContentColor.current
                )
            }
        }
    }
}