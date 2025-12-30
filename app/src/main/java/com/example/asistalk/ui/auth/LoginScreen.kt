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
import com.example.asistalk.network.LoginRequest
import com.example.asistalk.network.RetrofitClient
import com.example.asistalk.utils.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.asistalk.utils.TokenManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userPrefsRepo = remember {
        UserPreferencesRepository(context)
    }

    LaunchedEffect(Unit) {
        rememberMe = userPrefsRepo.rememberMeFlow.first()

        if (rememberMe) {
            username = userPrefsRepo.savedUsernameFlow.first()
            password = userPrefsRepo.savedPasswordFlow.first()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Header hijau
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    color = Color(0xFF00BFA6),
                    shape = RoundedCornerShape(
                        bottomStart = 100.dp,
                        bottomEnd = 100.dp
                    )
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
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
                        Toast.makeText(
                            context,
                            "Username dan password tidak boleh kosong",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            userPrefsRepo.saveLoginCredentials(
                                username,
                                password,
                                rememberMe
                            )


                            val request = LoginRequest(username, password)
                            val api = RetrofitClient.getInstance(context)
                            val response = api.loginUser(request)

                            if (response.success && response.token != null && response.user != null) {

                                TokenManager.saveToken(
                                    context = context,
                                    token = response.token
                                )

                                userPrefsRepo.saveToken(response.token)
                                userPrefsRepo.saveUserId(response.user.id)

                                val profile = response.user
                                val fullImageUrl =
                                    "http://10.0.2.2:3000${profile.profile_image}"


                                userPrefsRepo.saveFullProfile(
                                    fullName = profile.full_name,
                                    username = profile.username,
                                    email = profile.email,
                                    phone = profile.phone_number,
                                    birthDate = profile.birth_date,
                                    gender = profile.gender,
                                    profileImage = fullImageUrl
                                )

                                onLoginSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Login gagal",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Terjadi kesalahan: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00BFA6)
                ),
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
                    Text(
                        text = "Log In",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Belum punya akun? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = Color(0xFF00BFA6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}