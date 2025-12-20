package com.example.asistalk.ui.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ProfileViewModel : ViewModel() {

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
            uiState = uiState.copy(
                isLoading = true
            )

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
}
