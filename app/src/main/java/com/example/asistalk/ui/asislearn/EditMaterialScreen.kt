package com.example.asistalk.ui.asislearn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
// import androidx.lifecycle.viewmodel.compose.viewModel // <-- Dihapus jika menggunakan NavGraph injection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialScreen(
    navController: NavHostController,
    // ID Material yang akan diedit. Harus diteruskan melalui navigasi.
    materialId: String,
    viewModel: AsisLearnViewModel
) {
    // Memanggil fungsi untuk memuat data material saat composable pertama kali dibuat
    LaunchedEffect(materialId) {
        viewModel.loadMaterialForEdit(materialId)
    }

    // Menggunakan state dari ViewModel
    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val description by viewModel.description.collectAsState()
    val fileType by viewModel.fileType.collectAsState()
    val selectedFileUri by viewModel.selectedFileUri.collectAsState() // URI file baru/kosong
    val currentFileName by viewModel.currentFileName.collectAsState() // Nama file yang sudah ada
    val isLoading by viewModel.isLoading.collectAsState()
    val editEvent by viewModel.editEvent.collectAsState() // Event khusus untuk Edit

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk dropdown
    var isExpanded by remember { mutableStateOf(false) }
    val fileTypes = listOf("PDF", "Video", "Image", "Dokumen Lain")

    // Trigger navigasi ketika edit berhasil
    LaunchedEffect(editEvent) {
        if (editEvent == true) {
            Toast.makeText(context, "Material berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.consumeEditEvent()
        } else if (editEvent == false) {
            Toast.makeText(context, "Gagal memperbarui material.", Toast.LENGTH_SHORT).show()
            viewModel.consumeEditEvent()
        }
    }

    // DEFINISI filePickerLauncher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onFileSelected(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Material") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState) // Tambahkan scroll untuk form yang panjang
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {

                    // SUBJECT
                    LabelText("Subject")
                    InputTextField(
                        value = subject,
                        onValueChange = { viewModel.onSubjectChange(it) }
                    )
                    Spacer(Modifier.height(12.dp))

                    // TOPIC
                    LabelText("Topic")
                    InputTextField(
                        value = topic,
                        onValueChange = { viewModel.onTopicChange(it) }
                    )
                    Spacer(Modifier.height(12.dp))

                    // DESCRIPTION
                    LabelText("Description")
                    InputTextField(
                        value = description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        maxLines = 4
                    )
                    Spacer(Modifier.height(12.dp))

                    // FILE TYPE (DROPDOWN MENU)
                    LabelText("File Type")
                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = !isExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = fileType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            fileTypes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.onFileTypeChange(selectionOption)
                                        isExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // FILE PICKER/PENGGANTIAN FILE
                    LabelText("Change File (Optional)")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { filePickerLauncher.launch("*/*") },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // FIX: Buat salinan lokal dari nilai delegated properties
                        val currentSelectedUri: Uri? = selectedFileUri
                        val currentFileNameValue: String = currentFileName

                        // Tampilkan nama file yang sedang aktif (file yang sudah ada atau file baru)
                        val displayedFileName = when {
                            currentSelectedUri != null -> currentSelectedUri.lastPathSegment ?: "New file selected"
                            currentFileNameValue.isNotBlank() -> "Current file: ${currentFileNameValue}"
                            else -> "No file selected"
                        }

                        Text(
                            text = displayedFileName,
                            modifier = Modifier.padding(16.dp),
                            // Gunakan variabel lokal untuk pengecekan
                            color = if (currentSelectedUri == null && currentFileNameValue.isBlank())
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // UPDATE BUTTON
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
                                    // Panggil fungsi update, kirimkan ID material
                                    viewModel.updateMaterial(materialId)
                                },
                                // Minimal harus ada Subject dan Topic yang terisi
                                enabled = subject.isNotBlank()
                                        && topic.isNotBlank()
                                        && !isLoading,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Update Material",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp)) // Jarak di bawah card
        }
    }
}