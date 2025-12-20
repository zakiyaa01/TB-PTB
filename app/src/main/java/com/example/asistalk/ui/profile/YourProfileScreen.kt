package com.example.asistalk.ui.profile

import android.app.DatePickerDialog
import android.content.Context
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
    val context = LocalContext.current

    // ✅ INI YANG BENAR
    val uiState = profileViewModel.uiState

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            profileViewModel.onProfileImageChange(uri)
        }
    )

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            profileViewModel.toastMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uiState.profileImageUri ?: R.drawable.logo_asistalk_hijau,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            ProfileTextField(
                label = "Full Name",
                value = uiState.fullName,
                onValueChange = profileViewModel::onFullNameChange
            )

            ProfileTextField(
                label = "Birth Date",
                value = uiState.birthDate,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.clickable {
                    showDatePicker(context) { y, m, d ->
                        profileViewModel.onBirthDateChange(
                            "$d ${getMonthName(m)} $y"
                        )
                    }
                }
            )

            GenderSelector(
                selectedGender = uiState.gender,
                onGenderSelect = profileViewModel::onGenderChange
            )

            ProfileTextField(
                label = "Phone",
                value = uiState.phone,
                onValueChange = profileViewModel::onPhoneChange,
                keyboardType = KeyboardType.Phone
            )

            ProfileTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = profileViewModel::onEmailChange,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = profileViewModel::saveProfile,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }
        }
    }
}
