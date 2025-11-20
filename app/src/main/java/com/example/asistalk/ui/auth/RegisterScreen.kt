package com.example.asistalk.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.navigation.NavController // <- HAPUS IMPORT INI
import com.example.asistalk.R
import com.example.asistalk.network.RegisterRequest
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    // --- PERBAIKAN DI SINI ---
    onNavigateBackToLogin: () -> Unit // Ganti NavController dengan callback ini
) {
    // Variabel state untuk setiap input
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") } // Tambahkan state untuk username
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp)
            )

            Text(
                text = "Buat Akun Baru 🌸",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !isLoading
            )

            // --- PERBAIKAN PADA TOMBOL DAFTAR ---
            Button(
                onClick = {
                    // Validasi input
                    if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true // Tampilkan loading indicator
                    // Panggil API
                    scope.launch {
                        try {
                            val request = RegisterRequest(name, username, email, password)
                            val response = RetrofitClient.instance.registerUser(request)

                            if (response.success) {
                                Toast.makeText(context, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                                // Setelah sukses, kembali ke halaman login
                                onNavigateBackToLogin()
                            } else {
                                Toast.makeText(context, response.message ?: "Registrasi gagal", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false // Hentikan loading
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Daftar")
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sudah punya akun? ")
                // --- PERBAIKAN PADA NAVIGASI ---
                Text(
                    text = "Masuk",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateBackToLogin() } // Gunakan callback
                )
            }
        }
    }
}
