package com.example.asistalk.ui.asislearn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.asistalk.ui.theme.Primary // Menggunakan Primary Color dari theme
import com.example.asistalk.ui.theme.DarkText // Menggunakan DarkText Color dari theme

@Composable
fun UploadMaterial(navController: NavHostController) {
    // State untuk menyimpan input pengguna
    var subject by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("") }
    var uploadFile by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Menggunakan warna Background dari tema
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Judul Halaman: "Upload Material"
        Text(
            text = "Upload Material",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- Input Subject ---
        CustomInputField(
            label = "Subject",
            value = subject,
            onValueChange = { subject = it },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // --- Input Topic ---
        CustomInputField(
            label = "Topic",
            value = topic,
            onValueChange = { topic = it },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // --- Input Description ---
        CustomInputField(
            label = "Description",
            value = description,
            onValueChange = { description = it },
            minLines = 3,
            maxLines = 5
        )

        Spacer(Modifier.height(16.dp))

        // --- Input File Type ---
        CustomInputField(
            label = "File Type",
            value = fileType,
            onValueChange = { fileType = it },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // --- Input Upload File (Area besar) ---
        CustomInputField(
            label = "Upload File",
            value = uploadFile,
            onValueChange = { uploadFile = it },
            minLines = 5,
            readOnly = true, // Biasanya hanya menampilkan file yang sudah dipilih
            onClick = {
                // TODO: Logika pemilihan file (picker)
            }
        )

        Spacer(Modifier.height(24.dp))

        // --- Tombol Upload ---
        Button(
            onClick = { /* TODO: Logika upload */ },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                "Upload",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

// Composable Pembantu untuk elemen TextField yang berulang
@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column {
        // Label di atas TextField
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = DarkText,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Outlined TextField dengan desain kustom
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .heightIn(min = if (minLines > 1) 100.dp else 56.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            readOnly = readOnly,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                cursorColor = Primary,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}