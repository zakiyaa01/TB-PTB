package com.example.asistalk.ui.profile

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.asistalk.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourProfileScreen(navController: NavController) {
    // State untuk menampung nilai dari setiap input field
    var fullName by remember { mutableStateOf("Zakiya Aulia") }
    var birthDate by remember { mutableStateOf("1 July 2004") }
    var selectedGender by remember { mutableStateOf("Female") }
    var phone by remember { mutableStateOf("+628 2345 6789") }
    val labAccount by remember { mutableStateOf("zakiyaa@AsistLab.ac.id") } // Tidak bisa diedit
    var email by remember { mutableStateOf("zakiyaa911@gmail.com") }

    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState()) // Agar bisa di-scroll jika layar tidak cukup
        ) {
            // --- FOTO PROFIL & TOMBOL GANTI ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_asistalk_hijau), // Ganti dengan foto profil
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Change Profile Photo",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { /* TODO: Tambah logika ganti foto */ }
                    )
                }
            }

            // --- FORM INPUT ---
            ProfileTextField(
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it }
            )

            // --- INPUT TANGGAL LAHIR (DENGAN DATE PICKER) ---
            ProfileTextField(
                label = "Birth Date",
                value = birthDate,
                onValueChange = { /* Dibiarkan kosong karena di-handle oleh onClick */ },
                readOnly = true, // Agar keyboard tidak muncul
                modifier = Modifier.clickable {
                    showDatePicker(context) { year, month, day ->
                        birthDate = "$day ${getMonthName(month)} $year"
                    }
                }
            )

            // --- INPUT GENDER ---
            GenderSelector(
                selectedGender = selectedGender,
                onGenderSelect = { selectedGender = it }
            )

            ProfileTextField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it },
                keyboardType = KeyboardType.Phone
            )

            // Akun Lab tidak bisa diedit
            ProfileTextField(
                label = "Lab Account",
                value = labAccount,
                onValueChange = {},
                readOnly = true,
                enabled = false
            )

            ProfileTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = {
                    // Logika saat tombol simpan ditekan
                    val toastMessage = "Profile Updated:\n$fullName\n$email\n$selectedGender\n$phone"
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Profile", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Composable terpisah untuk Input Field agar lebih rapi
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

// Composable terpisah untuk pilihan Gender
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

// Fungsi helper untuk Date Picker
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

// Fungsi helper untuk nama bulan
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
