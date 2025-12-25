package com.example.asistalk.ui.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profileImageUri: Uri? = null,
    val fullName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val phone: String = "",
    val labAccount: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val toastMessage: String? = null
)

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile(
        fullName: String,
        email: String,
        username: String
    ) {
        uiState = uiState.copy(
            fullName = fullName,
            email = email,
            labAccount = username
        )
    }

    fun onProfileImageChange(uri: Uri?) {
        uiState = uiState.copy(profileImageUri = uri)
    }

    fun onFullNameChange(value: String) {
        uiState = uiState.copy(fullName = value)
    }

    fun onBirthDateChange(value: String) {
        uiState = uiState.copy(birthDate = value)
    }

    fun onGenderChange(value: String) {
        uiState = uiState.copy(gender = value)
    }

    fun onPhoneChange(value: String) {
        uiState = uiState.copy(phone = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun saveProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            kotlinx.coroutines.delay(1200)

            uiState = uiState.copy(
                isLoading = false,
                toastMessage = "Profile saved"
            )
        }
    }

    fun toastMessageShown() {
        uiState = uiState.copy(toastMessage = null)
    }

    // 🔥 INI YANG DIPERBAIKI (TANPA MERUSAK)
    // 🔥 VERSI FINAL FETCH PROFILE
    fun fetchProfile(userId: Int) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                val response = repository.getProfile(userId)
                val profile = response.user

                // 1️⃣ URL Gambar dengan Base URL
                val baseUrl = "http://10.0.2.2:3000"
                val fullImageUrl = "$baseUrl${profile.profile_image}"

                // 2️⃣ Perbaikan Format Tanggal (Birth Date)
                // Kita ambil 10 karakter pertama (YYYY-MM-DD) jika mengandung huruf 'T'
                val formattedDate = if (profile.birth_date.contains("T")) {
                    profile.birth_date.substring(0, 10)
                } else {
                    profile.birth_date
                }

                uiState = uiState.copy(
                    fullName = profile.full_name,
                    email = profile.email,
                    phone = profile.phone_number,
                    birthDate = formattedDate, // <--- Tanggal sudah rapi (YYYY-MM-DD)
                    gender = profile.gender,
                    labAccount = profile.username,
                    profileImageUri = Uri.parse(fullImageUrl), // <--- URL Gambar Lengkap
                    isLoading = false
                )

            } catch (e: Exception) {
                android.util.Log.e("FETCH_PROFILE", "Error: ${e.message}")
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}
