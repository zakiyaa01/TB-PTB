package com.example.asistalk.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asistalk.R
import com.example.asistalk.UserPreferencesRepository // <-- IMPORT BARU
import com.example.asistalk.network.LoginRequest
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // State untuk Checkbox "Remember Me"
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // instance dari repository preferensi kita
    val userPrefsRepo = remember { UserPreferencesRepository(context) }

    // Ambil data yang tersimpan dari DataStore
    // LaunchedEffect akan berjalan sekali saat layar dibuka
    LaunchedEffect(key1 = Unit) {
        // Ambil nilai awal untuk checkbox dan field login
        userPrefsRepo.rememberMeFlow.collect { isRemembered ->
            rememberMe = isRemembered
            if (isRemembered) {
                userPrefsRepo.savedUsernameFlow.collect { savedUsername ->
                    username = savedUsername
                }
                userPrefsRepo.savedPasswordFlow.collect { savedPassword ->
                    password = savedPassword
                }
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        // Bagian hijau atas (tidak berubah)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    color = Color(0xFF00BFA6),
                    shape = RoundedCornerShape(bottomStart = 100.dp, bottomEnd = 100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo_asistalk_putih),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Log In",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Konten utama (form login)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(180.dp))

            Text(
                text = "Welcome back!",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00695C)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hubungkan Checkbox dengan state 'rememberMe'
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe } // Buat seluruh baris bisa diklik
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { isChecked -> rememberMe = isChecked }
                    )
                    Text("Remember me", fontSize = 13.sp)
                }
                Text(
                    "Forgot Password?",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            // Simpan kredensial sebelum login
                            userPrefsRepo.saveLoginCredentials(username, password, rememberMe)

                            val request = LoginRequest(username, password)
                            val response = RetrofitClient.instance.loginUser(request)

                            if (response.success) {
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, response.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA6)),
                shape = RoundedCornerShape(24.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log In", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Belum punya akun? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = Color(0xFF00BFA6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}
