package com.example.asistalk.ui.asislearn

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMaterialScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel
) {
    val context = LocalContext.current

    // ===== STATE VIEWMODEL =====
    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val description by viewModel.description.collectAsState()
    val fileType by viewModel.fileType.collectAsState()
    val selectedFileUri by viewModel.selectedFileUri.collectAsState()
    val currentFileName by viewModel.currentFileName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadEvent by viewModel.uploadEvent.collectAsState()

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val fileTypes = listOf("PDF", "Video", "Image", "Dokumen")

    LaunchedEffect(Unit) {
        if (viewModel.editingMaterialId == null) {
            viewModel.resetInputStates()
        }
    }

    LaunchedEffect(uploadEvent) {
        uploadEvent?.let { success ->
            val actionName = if (viewModel.editingMaterialId == null) "Upload" else "Update"
            Toast.makeText(
                context,
                if (success) "$actionName berhasil" else "$actionName gagal",
                Toast.LENGTH_SHORT
            ).show()

            if (success) {
                navController.popBackStack()
            }
            viewModel.consumeUploadEvent()
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onFileSelected(uri, context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.editingMaterialId == null) "Upload Materi" else "Edit Materi",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            LabelText("Subject")
            InputTextField(
                value = subject,
                onValueChange = viewModel::onSubjectChange,
                placeholder = "Contoh: Matematika"
            )

            Spacer(Modifier.height(16.dp))

            LabelText("Topic")
            InputTextField(
                value = topic,
                onValueChange = viewModel::onTopicChange,
                placeholder = "Contoh: Aljabar"
            )

            Spacer(Modifier.height(16.dp))

            LabelText("Description (Optional)")
            InputTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = "Deskripsi singkat materi",
                maxLines = 4
            )

            Spacer(Modifier.height(16.dp))

            LabelText("File Type")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = fileType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    fileTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onFileTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LabelText("File")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { filePicker.launch("*/*") },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudUpload, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = currentFileName.ifBlank { "Pilih file..." },
                        color = if (currentFileName.isBlank()) Color.Gray else Color.Black,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    // Validasi: Subject & Topic wajib.
                    // File wajib hanya untuk upload baru (editingMaterialId == null).
                    enabled = subject.isNotBlank() && topic.isNotBlank() &&
                            (viewModel.editingMaterialId != null || selectedFileUri != null),
                    onClick = {
                        if (viewModel.editingMaterialId == null) {
                            // Panggil tanpa parameter token (handled by Interceptor)
                            viewModel.uploadMaterial(context)
                        } else {
                            // Panggil tanpa parameter token (handled by Interceptor)
                            viewModel.updateMaterial(context)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (viewModel.editingMaterialId == null) "UPLOAD SEKARANG" else "SIMPAN PERUBAHAN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* =======================
   UI HELPERS (Penting agar tidak error)
   ======================= */

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            if (placeholder.isNotBlank())
                Text(placeholder, color = Color.Gray)
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = maxLines,
        singleLine = maxLines == 1,
        shape = RoundedCornerShape(12.dp)
    )
}