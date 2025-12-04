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
    val topic: String = "",
    val fileUri: String = ""
)

class AsisLearnViewModel : ViewModel() {

    // ====================================================================
    // 1. STATE BERSAMA (DAFTAR & DETAIL)
    // ====================================================================

    // --- STATE DAFTAR MATERI UTAMA (Data Master) ---
    // Gunakan MutableList untuk memudahkan penambahan data setelah upload
    private val _allMaterials = mutableListOf(
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

    // Gunakan ini untuk menyimpan dan mempublikasikan data yang sudah difilter/diurutkan (yang ditampilkan di UI)
    private val _materials = MutableStateFlow(_allMaterials.toList())
    val materials = _materials.asStateFlow()

    // --- STATE DETAIL MATERI (Tidak Berubah) ---
    private val _selectedMaterial = MutableStateFlow<MaterialItem?>(null)
    val selectedMaterial = _selectedMaterial.asStateFlow()

    fun getMaterialByTitle(title: String) {
        // Cari di daftar yang saat ini ditampilkan
        val material = _materials.value.find { it.title == title }
        _selectedMaterial.value = material
    }

    // ====================================================================
    // 2. FUNGSI PENCARIAN BARU
    // ====================================================================

    /**
     * Memfilter daftar materi berdasarkan judul atau topik.
     */
    fun searchQuery(query: String) {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) {
            // Jika kosong, tampilkan semua materi dari master list
            _materials.value = _allMaterials.toList()
        } else {
            // Filter materi dari master list
            _materials.value = _allMaterials.filter { item ->
                item.title.lowercase().contains(normalizedQuery) ||
                        item.topic.lowercase().contains(normalizedQuery)
            }
        }
    }

    // ====================================================================
    // 3. STATE INPUT UPLOAD (Tidak Berubah)
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
    // 4. UPDATE STATE FUNCTIONS (Tidak Berubah)
    // ====================================================================

    fun onSubjectChange(value: String) { _subject.value = value }
    fun onTopicChange(value: String) { _topic.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onFileTypeChange(value: String) { _fileType.value = value }
    fun onFileSelected(uri: Uri?) { _selectedFileUri.value = uri }
    fun consumeUploadEvent() { _uploadEvent.value = null }

    // ====================================================================
    // 5. UPLOAD FUNCTION (DIPERBAIKI)
    // ====================================================================
    fun uploadMaterial() {
        if (_subject.value.isBlank() || _topic.value.isBlank() || _selectedFileUri.value == null) {
            _uploadEvent.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val iconRes = when (_fileType.value.lowercase()) {
                "pdf" -> R.drawable.ic_pdf
                "video" -> R.drawable.ic_video
                "image" -> R.drawable.ic_image
                else -> R.drawable.ic_pdf
            }

            // SIMULASI UPLOAD
            delay(2000)

            val newMaterial = MaterialItem(
                title = _subject.value,
                type = _fileType.value,
                author = "Anda",
                icon = iconRes,
                description = _description.value,
                topic = _topic.value,
                fileUri = _selectedFileUri.value.toString()
            )

            // 1. Tambahkan materi baru ke daftar master (_allMaterials)
            _allMaterials.add(0, newMaterial) // Tambahkan di posisi terdepan

            // 2. Perbarui state _materials (yang ditampilkan di UI)
            _materials.value = _allMaterials.toList()

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