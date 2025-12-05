package com.example.asistalk.ui.asislearn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.asistalk.R
// Import untuk image loading dari URI
// ASUMSI MENGGUNAKAN COIL (Anda harus menambahkannya ke build.gradle)
// import coil.compose.AsyncImage
// import coil.request.ImageRequest
// import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LihatScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel,
    materialTitle: String
) {
    // ... (State & LaunchedEffect tetap sama)
    val selectedMaterial by viewModel.selectedMaterial.collectAsState()

    LaunchedEffect(materialTitle) {
        viewModel.getMaterialByTitle(materialTitle)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedMaterial?.title ?: "Detail Materi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        val material = selectedMaterial

        if (material == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Materi tidak ditemukan.", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judul dan Pengarang
            Text(
                text = material.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = material.author,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Subjudul - DIPERBAIKI: Menggunakan field 'topic' dari ViewModel (Asumsi MaterialItem diperbarui)
            // Jika Anda tidak menyimpan Topic di MaterialItem, Anda dapat menggunakan field lain.
            Text(
                text = material.topic, // <--- MENGAMBIL DATA TOPIC DARI VIEWMODEL
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Gambar (Thumbnail) - DIPERBAIKI: Menampilkan URI jika ada, jika tidak, gunakan Icon.
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(16f / 9f)
            ) {
                if (material.fileUri.isNotEmpty() && material.type.lowercase() in listOf("image", "pdf")) {
                    // Jika file adalah gambar, gunakan pustaka image loading (Contoh Coil)
                    // Jika file adalah PDF/Video, Anda bisa menggunakan thumbnail atau placeholder yang lebih spesifik.

                    // KODE COIL (ASUMSI):
                    /*
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(material.fileUri))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Uploaded File Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    */

                    // KODE PENGGANTI (Jika Coil tidak diimpor/digunakan):
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Preview File URI: ${material.fileUri.substring(0, 30)}...",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                } else {
                    // Fallback ke icon jika URI kosong atau tipe file tidak dapat dipreview
                    Image(
                        painter = painterResource(id = material.icon),
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Fit, // Ubah ke Fit agar icon terlihat jelas
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Deskripsi
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))) {
                Text(
                    text = "Deskripsi:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    // DIPERBAIKI: Hanya tampilkan deskripsi jika ada, hapus dummy text
                    text = material.description.ifBlank { "Tidak ada deskripsi tersedia." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(32.dp))

            // Tombol Aksi (Open Document dan Download)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Open Document - DIPERBAIKI: Gunakan tipe file untuk label
                val openLabel = if (material.type == "PDF") "Open Document" else "Open ${material.type}"
                Button(
                    onClick = { /* Handle Open Document/File menggunakan URI material.fileUri */ },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(painterResource(id = material.icon), contentDescription = "Open", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(openLabel, color = MaterialTheme.colorScheme.onPrimary)
                }

                // Download
                OutlinedButton(
                    onClick = { /* Handle Download menggunakan URI material.fileUri */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                ) {
                    Icon(painterResource(id = R.drawable.ic_download), contentDescription = "Download", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Download", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}