package com.example.asistalk.ui.asislearn

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMaterialScreen(
    navController: NavHostController,
    viewModel: AsisLearnViewModel = viewModel()
) {

    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val description by viewModel.description.collectAsState()
    val fileType by viewModel.fileType.collectAsState()
    val selectedFileUri by viewModel.selectedFileUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadEvent by viewModel.uploadEvent.collectAsState()

    // Trigger navigasi ketika upload berhasil
    LaunchedEffect(uploadEvent) {
        if (uploadEvent == true) {
            navController.popBackStack()
            viewModel.consumeUploadEvent()
        }
    }

    // KOREKSI: DEFINISI filePickerLauncher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onFileSelected(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Material") },
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

                    // FILE TYPE
                    LabelText("File Type")
                    InputTextField(
                        value = fileType,
                        onValueChange = { viewModel.onFileTypeChange(it) }
                    )
                    Spacer(Modifier.height(12.dp))

                    // FILE PICKER
                    LabelText("Upload File")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            // PEMANGGILAN filePickerLauncher sekarang valid
                            .clickable { filePickerLauncher.launch("*/*") },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        val fileName =
                            selectedFileUri?.lastPathSegment ?: "No file selected"

                        Text(
                            text = fileName,
                            modifier = Modifier.padding(16.dp),
                            color = if (selectedFileUri == null)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // UPLOAD BUTTON
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
                                    viewModel.uploadMaterial()
                                },
                                enabled = subject.isNotBlank()
                                        && topic.isNotBlank()
                                        && selectedFileUri != null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Upload",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
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
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}