package com.example.asistalk.ui.auth

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asistalk.R
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Composable
fun RegisterScreen(
    onNavigateBackToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // IMAGE PICKER
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            profileImageUri = uri
        }

    Box(modifier = Modifier.fillMaxSize()) {

        // HEADER
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
                    text = "Sign Up",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(300.dp))

            Text(
                text = "Create Your Account!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00695C)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor Telepon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Jenis Kelamin (L / P)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { imagePicker.launch("image/*") }) {
                Text(if (profileImageUri == null) "Pilih Foto Profil" else "Foto Dipilih ✔")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (
                        fullName.isBlank() || username.isBlank() || email.isBlank() ||
                        phone.isBlank() || birthDate.isBlank() ||
                        gender.isBlank() || password.isBlank() ||
                        profileImageUri == null
                    ) {
                        Toast.makeText(context, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val imageFile = getFileFromUri(context, profileImageUri!!)
                            val imageRequest =
                                imageFile.asRequestBody("image/*".toMediaType())

                            val imagePart = MultipartBody.Part.createFormData(
                                "profile_image",
                                imageFile.name,
                                imageRequest
                            )

                            val response = RetrofitClient.instance.registerUser(
                                fullName.toRequestBody("text/plain".toMediaType()),
                                username.toRequestBody("text/plain".toMediaType()),
                                email.toRequestBody("text/plain".toMediaType()),
                                phone.toRequestBody("text/plain".toMediaType()),
                                password.toRequestBody("text/plain".toMediaType()),
                                birthDate.toRequestBody("text/plain".toMediaType()),
                                gender.toRequestBody("text/plain".toMediaType()),
                                imagePart
                            )

                            if (response.success) {
                                Toast.makeText(context, "Registrasi berhasil", Toast.LENGTH_LONG).show()
                                onNavigateBackToLogin()
                            } else {
                                Toast.makeText(context, response.message ?: "Gagal", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA6))
            ) {
                Text("Sign Up", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text("Sudah punya akun? ")
                Text(
                    "Login",
                    color = Color(0xFF00BFA6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateBackToLogin() }
                )
            }
        }
    }
}

/**
 * 🔥 HELPER WAJIB
 * Convert content:// Uri → File (AMAN UNTUK ANDROID MODERN)
 */
fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Tidak bisa membuka file")

    val tempFile = File.createTempFile("profile_", ".jpg", context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}