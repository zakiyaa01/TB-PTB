package com.example.asistalk.ui.asishub

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun EditPostScreen(navController: NavHostController /* Nanti tambahkan postId: String */) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit Post", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        // Biasanya, textfield ini akan diisi dengan data postingan yang ada
        OutlinedTextField(
            value = "Tutorial lengkap instalasi Ubuntu Server…", // Contoh data yang ada
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(Modifier.height(20.dp))

        // Tombol Simpan dan Batal
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Batal")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    // Logika untuk menyimpan perubahan
                    navController.popBackStack() // Kembali setelah menyimpan
                }
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}
