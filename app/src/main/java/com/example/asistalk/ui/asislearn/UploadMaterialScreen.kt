package com.example.asistalk.ui.asislearn

// Pastikan semua import ini ada
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMaterialScreen(navController: NavHostController) {
    // --- STATE MANAGEMENT ---
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("PDF") } // Bisa jadi dropdown nanti

    // State untuk menyimpan URI file yang dipilih, bukan hanya nama
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk mengontrol loading indicator
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // --- LOGIKA PEMILIH FILE ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Ketika pengguna memilih file, URI-nya akan disimpan di sini
        selectedFileUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Material") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        LabelText("Subject")
                        InputTextField(value = subject, onValueChange = { subject = it })

                        Spacer(Modifier.height(12.dp))

                        LabelText("Topic")
                        InputTextField(value = topic, onValueChange = { topic = it })

                        Spacer(Modifier.height(12.dp))

                        LabelText("Description")
                        InputTextField(value = description, onValueChange = { description = it }, maxLines = 4)

                        Spacer(Modifier.height(12.dp))

                        // (Saran) File Type bisa dibuat menjadi dropdown/pilihan
                        LabelText("File Type")
                        InputTextField(value = fileType, onValueChange = { fileType = it })

                        Spacer(Modifier.height(12.dp))

                        // --- KOTAK UPLOAD FILE (Fungsional) ---
                        LabelText("Upload File")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .clickable {
                                    // Buka pemilih file sistem Android
                                    // Anda bisa spesifikasikan tipe file, mis: "application/pdf"
                                    filePickerLauncher.launch("*/*")
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // Dapatkan nama file dari URI jika ada
                            val fileName = selectedFileUri?.lastPathSegment ?: "No file selected"

                            Text(
                                text = fileName,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                // Menggunakan warna dari theme Anda
                                color = if (selectedFileUri == null) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Button(
                                    onClick = {
                                        // TODO: Logika sebenarnya untuk mengirim data
                                        // Kita akan memicu loading state di sini
                                        isLoading = true

                                        // Buat fungsi untuk handle upload
                                        // uploadMaterial(subject, topic, description, selectedFileUri)

                                        // SIMULASI: Anggap upload butuh 2 detik
                                        // Ganti bagian ini dengan logika upload Anda (misal ke Firebase)
                                        // Setelah selesai, matikan loading dan kembali
                                        // Contoh:
                                        // viewModel.upload(subject, topic, description, selectedFileUri) { success ->
                                        //      isLoading = false
                                        //      if (success) navController.popBackStack()
                                        // }
                                        navController.popBackStack()
                                    },
                                    // Tombol dinonaktifkan jika input belum lengkap
                                    enabled = subject.isNotBlank() && topic.isNotBlank() && selectedFileUri != null,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) // Warna saat non-aktif
                                    )
                                ) {
                                    Text(
                                        text = "Upload",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = maxLines == 1,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            // Menggunakan warna dari theme untuk teks dan cursor
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}