package com.example.asistalk.ui.asishub

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreatePostScreen(
    navController: NavHostController,
    vm: AsisHubViewModel
) {
    // 2. Buat state untuk menampung teks dari TextField
    var postContent by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create New Post", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // 3. Hubungkan TextField dengan state
        OutlinedTextField(
            value = postContent,
            onValueChange = { postContent = it },
            placeholder = { Text("Apa yang ingin anda diskusikan?") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Beri bobot agar mengisi ruang
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = { /* TODO */ }) { Text("📷 Photo") }
            Spacer(Modifier.width(10.dp))
            Button(onClick = { /* TODO */ }) { Text("🎬 Video") }
        }

        Spacer(Modifier.height(20.dp))

        // 4. Saat tombol "Posting" diklik
        Button(
            onClick = {
                // Jangan posting jika kosong
                if (postContent.isNotBlank()) {
                    // Panggil fungsi addPost dari ViewModel
                    vm.addPost(author = "Zakiya Aulia", content = postContent) // Ganti nama author jika perlu
                    // Kembali ke layar sebelumnya
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Posting")
        }
    }
}
