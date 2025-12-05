package com.example.asistalk.ui.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class untuk menampung semua data yang dibutuhkan oleh UI
data class ProfileUiState(
    var profileImageUri: Uri? = null, // <-- Untuk menyimpan URI gambar yang dipilih
    var fullName: String = "",
    var birthDate: String = "",
    var gender: String = "Female",
    var phone: String = "",
    val labAccount: String = "", // Tidak bisa diedit
    var email: String = "",
    var isLoading: Boolean = false,
    var toastMessage: String? = null
)

// ViewModel untuk mengelola state dan logika YourProfileScreen
class ProfileViewModel : ViewModel() {

    // 'uiState' ini akan menjadi satu-satunya sumber data (single source of truth) untuk UI
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        // Saat ViewModel pertama kali dibuat, kita panggil fungsi untuk memuat data pengguna
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // --- NANTINYA: Di sini Anda akan memanggil API dari backend untuk mendapatkan data ---
            // Contoh: val userProfile = repository.getUserProfile()

            // Untuk saat ini, kita gunakan data awal seperti di kode Anda
            uiState = uiState.copy(
                profileImageUri = null, // Awalnya tidak ada gambar yang dipilih
                fullName = "Zakiya Aulia",
                birthDate = "1 July 2004",
                gender = "Female",
                phone = "+628 2345 6789",
                labAccount = "zakiyaa@AsistLab.ac.id",
                email = "zakiyaa911@gmail.com"
            )
        }
    }

    // --- FUNGSI-FUNGSI AKSI DARI UI ---

    // Aksi ini dipanggil dari UI saat pengguna selesai memilih gambar
    fun onProfileImageChange(newUri: Uri?) {
        uiState = uiState.copy(profileImageUri = newUri)
    }

    fun onFullNameChange(newName: String) {
        uiState = uiState.copy(fullName = newName)
    }

    fun onBirthDateChange(newDate: String) {
        uiState = uiState.copy(birthDate = newDate)
    }

    fun onGenderChange(newGender: String) {
        uiState = uiState.copy(gender = newGender)
    }

    fun onPhoneChange(newPhone: String) {
        uiState = uiState.copy(phone = newPhone)
    }

    fun onEmailChange(newEmail: String) {
        uiState = uiState.copy(email = newEmail)
    }

    // Fungsi ini dipanggil saat tombol "Save Profile" ditekan
    fun saveProfile() {
        viewModelScope.launch {
            // 1. Tampilkan indikator loading di UI
            uiState = uiState.copy(isLoading = true)

            // 2. --- NANTINYA: Kirim data 'uiState' ke backend menggunakan Retrofit ---
            // Termasuk mengunggah gambar dari `uiState.profileImageUri` jika tidak null
            delay(1500) // Simulasi jeda waktu saat request ke jaringan

            // 3. Setelah selesai, sembunyikan loading dan tampilkan pesan
            uiState = uiState.copy(
                isLoading = false,
                toastMessage = "Profile updated successfully!"
            )
        }
    }

    // Fungsi untuk memberi tahu ViewModel bahwa pesan toast sudah ditampilkan
    fun toastMessageShown() {
        uiState = uiState.copy(toastMessage = null)
    }
}
