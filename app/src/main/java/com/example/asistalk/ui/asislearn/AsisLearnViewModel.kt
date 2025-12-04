package com.example.asistalk.ui.asislearn

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asistalk.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class yang digunakan secara konsisten oleh semua Composable
data class MaterialItem(
    val title: String,
    val type: String,
    val author: String,
    val icon: Int,
    val description: String = "",
    val topic: String = "", // HARUS ADA
    val fileUri: String = "" // HARUS ADA
)

class AsisLearnViewModel : ViewModel() {

    // ====================================================================
    // 1. STATE BERSAMA (DAFTAR & DETAIL)
    // ====================================================================

    // --- STATE DAFTAR MATERI (SHARED STATE) ---
    private val _materials = MutableStateFlow(
        listOf(
            // Data dummy awal DIPERBARUI untuk menyertakan TOPIC dan FILE URI
            MaterialItem(
                title = "Modul 5 Praktikum PTB",
                type = "PDF",
                author = "Alexander",
                icon = R.drawable.ic_pdf,
                topic = "Modul Pertemuan 5: Material Design",
                description = "Modul dasar untuk praktikum perancangan tampilan berbasis Android Compose."
            ),
            MaterialItem(
                title = "Database Praktikum",
                type = "Video",
                author = "Alexander",
                icon = R.drawable.ic_video,
                topic = "Tutorial Dasar Penggunaan SQLite",
                description = "Video panduan langkah demi langkah tentang integrasi database lokal."
            ),
            MaterialItem(
                title = "Modul Rancang Bangun blabla",
                type = "Image",
                author = "Alexander",
                icon = R.drawable.ic_image,
                topic = "Diagram UML Proyek",
                description = "Dokumen yang berisi diagram dan arsitektur proyek."
            ),
        )
    )
    val materials = _materials.asStateFlow()

    // --- STATE DETAIL MATERI ---
    private val _selectedMaterial = MutableStateFlow<MaterialItem?>(null)
    val selectedMaterial = _selectedMaterial.asStateFlow()

    fun getMaterialByTitle(title: String) {
        val material = _materials.value.find { it.title == title }
        _selectedMaterial.value = material
    }

    // ====================================================================
    // 2. STATE INPUT UPLOAD
    // ====================================================================

    private val _subject = MutableStateFlow("")
    val subject = _subject.asStateFlow()

    private val _topic = MutableStateFlow("")
    val topic = _topic.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _fileType = MutableStateFlow("PDF")
    val fileType = _fileType.asStateFlow()

    private val _selectedFileUri = MutableStateFlow<Uri?>(null)
    val selectedFileUri = _selectedFileUri.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uploadEvent = MutableStateFlow<Boolean?>(null)
    val uploadEvent = _uploadEvent.asStateFlow()

    // ====================================================================
    // 3. UPDATE STATE FUNCTIONS
    // ====================================================================

    fun onSubjectChange(value: String) { _subject.value = value }
    fun onTopicChange(value: String) { _topic.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onFileTypeChange(value: String) { _fileType.value = value }
    fun onFileSelected(uri: Uri?) { _selectedFileUri.value = uri }
    fun consumeUploadEvent() { _uploadEvent.value = null }

    // ====================================================================
    // 4. UPLOAD FUNCTION (DIPERBAIKI)
    // ====================================================================
    fun uploadMaterial() {
        if (_subject.value.isBlank() || _topic.value.isBlank() || _selectedFileUri.value == null) {
            _uploadEvent.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            // Tentukan ikon berdasarkan tipe file
            val iconRes = when (_fileType.value.lowercase()) {
                "pdf" -> R.drawable.ic_pdf
                "video" -> R.drawable.ic_video
                "image" -> R.drawable.ic_image
                else -> R.drawable.ic_pdf
            }

            // SIMULASI UPLOAD
            delay(2000)

            // Buat dan tambahkan materi baru ke daftar.
            // PERBAIKAN: Menyertakan TOPIC dan FILE URI dari input.
            val newMaterial = MaterialItem(
                title = _subject.value,
                type = _fileType.value,
                author = "Anda", // Asumsi user yang upload
                icon = iconRes,
                description = _description.value,
                topic = _topic.value, // <-- MEMASTIKAN TOPIC DIISI
                fileUri = _selectedFileUri.value.toString() // <-- MEMASTIKAN FILE URI DIISI
            )

            // Tambahkan materi baru di urutan teratas (membuat list baru)
            _materials.value = listOf(newMaterial) + _materials.value

            // Reset field input
            _subject.value = ""
            _topic.value = ""
            _description.value = ""
            _selectedFileUri.value = null

            _isLoading.value = false
            _uploadEvent.value = true
        }
    }
}