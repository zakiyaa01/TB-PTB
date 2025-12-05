package com.example.asistalk.ui.profile

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.asistalk.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState = profileViewModel.uiState
    val context = LocalContext.current

    // Launcher untuk membuka galeri foto dan mendapatkan hasilnya
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            // Saat pengguna selesai memilih, teruskan URI ke ViewModel
            profileViewModel.onProfileImageChange(uri)
        }
    )

    // Efek ini akan berjalan ketika `toastMessage` di ViewModel berubah (tidak null)
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            profileViewModel.toastMessageShown() // Beri tahu ViewModel pesan sudah ditampilkan
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- BAGIAN FOTO PROFIL (DIPERBARUI) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Gunakan Coil AsyncImage untuk menampilkan gambar dari URI atau drawable
                    AsyncImage(
                        model = uiState.profileImageUri ?: R.drawable.logo_asistalk_hijau,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { // Buat gambar bisa diklik untuk membuka galeri
                                // --- PERBAIKAN DI SINI ---
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Change Profile Photo",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { // Teks ini juga bisa diklik
                            // --- DAN PERBAIKAN DI SINI ---
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }
            }

            // --- FORM INPUT DIHUBUNGKAN KE VIEWMODEL (TIDAK BERUBAH) ---
            ProfileTextField(
                label = "Full Name",
                value = uiState.fullName,
                onValueChange = { profileViewModel.onFullNameChange(it) }
            )

            ProfileTextField(
                label = "Birth Date",
                value = uiState.birthDate,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.clickable {
                    showDatePicker(context) { year, month, day ->
                        val newDate = "$day ${getMonthName(month)} $year"
                        profileViewModel.onBirthDateChange(newDate)
                    }
                }
            )

            GenderSelector(
                selectedGender = uiState.gender,
                onGenderSelect = { profileViewModel.onGenderChange(it) }
            )

            ProfileTextField(
                label = "Phone Number",
                value = uiState.phone,
                onValueChange = { profileViewModel.onPhoneChange(it) },
                keyboardType = KeyboardType.Phone
            )

            ProfileTextField(
                label = "Lab Account",
                value = uiState.labAccount,
                onValueChange = {}, // <-- Perbaikan typo dari 'onValue-change'
                readOnly = true,
                enabled = false
            )

            ProfileTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = { profileViewModel.onEmailChange(it) },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN YANG DINAMIS (TIDAK BERUBAH) ---
            Button(
                onClick = {
                    profileViewModel.saveProfile()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Save Profile", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ... (Sisa kode helper tidak perlu diubah)
// --- HELPER COMPOSABLES & FUNCTIONS (TIDAK ADA PERUBAHAN) ---

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        readOnly = readOnly,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun GenderSelector(selectedGender: String, onGenderSelect: (String) -> Unit) {
    val genders = listOf("Male", "Female")
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Gender", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            genders.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onGenderSelect(gender) }
                        .padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = { onGenderSelect(gender) }
                    )
                    Text(text = gender)
                }
            }
        }
    }
}

private fun showDatePicker(context: Context, onDateSelected: (Int, Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun getMonthName(month: Int): String {
    return arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[month]
}

@Preview(showBackground = true)
@Composable
fun YourProfileScreenPreview() {
    YourProfileScreen(navController = rememberNavController())
}
