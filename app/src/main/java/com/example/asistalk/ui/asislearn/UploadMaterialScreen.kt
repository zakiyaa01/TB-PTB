package com.example.asistalk.ui.asislearn

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Hapus warning API Eksperimental
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMaterialScreen(navController: NavHostController) {

    // Data input
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("PDF") }
    var selectedFileName by remember { mutableStateOf("No file selected") }

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

                // White card container
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
                        // Subject - KOREKSI PEMANGGILAN FUNGSI
                        LabelText("Subject")
                        InputTextField(
                            value = subject,
                            onValueChange = { subject = it } // <-- KOREKSI
                        )

                        Spacer(Modifier.height(12.dp))

                        // Topic - KOREKSI PEMANGGILAN FUNGSI
                        LabelText("Topic")
                        InputTextField(
                            value = topic,
                            onValueChange = { topic = it } // <-- KOREKSI
                        )

                        Spacer(Modifier.height(12.dp))

                        // Description - KOREKSI PEMANGGILAN FUNGSI
                        LabelText("Description")
                        InputTextField(
                            value = description,
                            onValueChange = { description = it }, // <-- KOREKSI
                            maxLines = 4
                        )

                        Spacer(Modifier.height(12.dp))

                        // File Type - KOREKSI PEMANGGILAN FUNGSI
                        LabelText("File Type")
                        InputTextField(
                            value = fileType,
                            onValueChange = { fileType = it } // <-- KOREKSI
                        )

                        Spacer(Modifier.height(12.dp))

                        // Upload File (Simulasi Tombol Pilih File)
                        LabelText("Upload File")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .clickable {
                                    // SIMULASI: Ganti nama file setelah klik
                                    selectedFileName = "file_${System.currentTimeMillis()}.pdf"
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = selectedFileName,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = if (selectedFileName == "No file selected") Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                        }


                        Spacer(Modifier.height(20.dp))

                        // Upload button (right aligned)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (subject.isNotBlank() && topic.isNotBlank() && selectedFileName != "No file selected") {
                                        // TODO: Logika sebenarnya untuk mengirim data
                                        navController.popBackStack()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
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
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}