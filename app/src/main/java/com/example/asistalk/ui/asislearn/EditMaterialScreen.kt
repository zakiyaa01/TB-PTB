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
fun EditMaterialScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel
) {
    val context = LocalContext.current

    // Sinkronisasi State dengan ViewModel
    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val description by viewModel.description.collectAsState()
    val fileType by viewModel.fileType.collectAsState()
    val currentFileName by viewModel.currentFileName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadEvent by viewModel.uploadEvent.collectAsState()

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val fileTypes = listOf("PDF", "Video", "Image", "Dokumen")

    LaunchedEffect(uploadEvent) {
        uploadEvent?.let { success ->
            Toast.makeText(
                context,
                if (success) "Materi berhasil diperbarui" else "Gagal memperbarui materi",
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
                title = { Text("Edit Materi", fontWeight = FontWeight.Bold) },
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
                onValueChange = viewModel::onSubjectChange
            )

            Spacer(Modifier.height(16.dp))

            LabelText("Topic")
            InputTextField(
                value = topic,
                onValueChange = viewModel::onTopicChange
            )

            Spacer(Modifier.height(16.dp))

            LabelText("Description")
            InputTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
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

            LabelText("File (Kosongkan jika tidak ingin ganti file)")
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
                        text = if (currentFileName.isBlank()) "Pilih file baru..." else currentFileName,
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    // Validasi
                    enabled = subject.isNotBlank() && topic.isNotBlank(),
                    onClick = {
                        viewModel.updateMaterial(context)
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("SIMPAN PERUBAHAN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}