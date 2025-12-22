package com.example.asistalk.ui.asislearn

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asistalk.ui.theme.Primary
import com.example.asistalk.ui.theme.Secondary
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    materialId: Int,
    navController: NavController,
    viewModel: AsisLearnViewModel
) {
    val context = LocalContext.current
    val material by viewModel.selectedMaterial.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.getDetailFromList(materialId)
    }

    // Fungsi openFile yang simpel tapi ampuh untuk emulator
    // Fungsi openFile yang lebih tangguh untuk Emulator
    fun openFile(url: String) {
        try {
            // 1. Sesuaikan URL untuk Emulator
            val adjustedUrl = url.replace("localhost", "10.0.2.2")
            val uri = Uri.parse(adjustedUrl)

            // 2. Ambil ekstensi file untuk menentukan MimeType
            val extension = MimeTypeMap.getFileExtensionFromUrl(adjustedUrl)
            val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())

            // 3. Buat Intent
            val intent = Intent(Intent.ACTION_VIEW)

            if (type != null) {
                // Jika tipe file dikenali (pdf, mp4, dll), set data dan tipenya
                intent.setDataAndType(uri, type)
            } else {
                // Jika tidak dikenali, buka sebagai link umum
                intent.data = uri
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // 4. Paksa gunakan Chooser agar user bisa memilih MuPDF atau Chrome
            val chooser = Intent.createChooser(intent, "Buka dengan...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooser)

        } catch (e: Exception) {
            // Fallback: Jika gagal total, paksa buka Browser Chrome tanpa MimeType
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("localhost", "10.0.2.2")))
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Tidak ada aplikasi (MuPDF/Browser) yang merespon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Materi", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        material?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8FAFC))
            ) {
                // --- HEADER VISUAL ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(Primary, Secondary)),
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(110.dp)
                        ) {
                            Icon(
                                imageVector = if (item.file_type.uppercase() == "PDF")
                                    Icons.Default.PictureAsPdf else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(24.dp).size(48.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = item.file_type.uppercase(),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // --- INFO CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-40).dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(item.subject, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.topic, fontSize = 16.sp, color = Primary, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                        Spacer(Modifier.height(24.dp))

                        Text("Deskripsi Materi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = item.description ?: "Tidak ada deskripsi.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B),
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.Person, null, tint = Primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Pengunggah", fontSize = 11.sp, color = Color.Gray)
                                Text(item.author_name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        // Tombol Lihat
                        Button(
                            onClick = { openFile(item.file_path) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Default.Visibility, null)
                            Spacer(Modifier.width(10.dp))
                            Text("Lihat Materi Lengkap", fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(12.dp))

                        // Tombol Download
                        OutlinedButton(
                            onClick = { openFile(item.file_path) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary)
                        ) {
                            Icon(Icons.Default.Download, null, tint = Primary)
                            Spacer(Modifier.width(10.dp))
                            Text("Download File", fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        }
    }
}