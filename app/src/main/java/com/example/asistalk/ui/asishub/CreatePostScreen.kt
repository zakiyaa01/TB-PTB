package com.example.asistalk.ui.asishub

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val selectedImageUri by vm.selectedImageUri.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(selectedImageUri) {
        imageUri = selectedImageUri
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                vm.onImageSelected(it)
            }
        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Buat Postingan Baru",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = postContent,
            onValueChange = { postContent = it },
            placeholder = { Text("Apa yang ingin anda diskusikan?") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            Button(onClick = {
                pickMediaLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }) {
                Icon(Icons.Default.PhotoLibrary, null)
                Spacer(Modifier.width(8.dp))
                Text("Galeri")
            }

            Button(onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Default.CameraAlt, null)
                Spacer(Modifier.width(8.dp))
                Text("Kamera")
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                vm.createPost(
                    content = postContent,
                    imageUri = imageUri
                )
                navController.popBackStack()
            },
            enabled = postContent.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Posting")
        }
    }
}
