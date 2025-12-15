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
        val material = _materials.value.find { it.title == title }
        _selectedMaterial.value = material
    }

    // ====================================================================
    // 2. FUNGSI FILTERING BARU (Menggantikan searchQuery)
    // ====================================================================

    /**
     * Memfilter daftar materi berdasarkan query pencarian dan tab yang dipilih.
     * @param query Teks pencarian
     * @param selectedTab Indeks tab: 0=All, 1=My Material, 2=Download
     */
    fun filterMaterials(query: String, selectedTab: Int) {
        val normalizedQuery = query.trim().lowercase()
        var filteredList: List<MaterialItem> = _allMaterials

        // 1. FILTER BERDASARKAN TAB
        filteredList = when (selectedTab) {
            0 -> _allMaterials.toList() // ALL: Tampilkan semua
            // MY MATERIAL: Filter yang authornya 'Anda' (Asumsi user yang upload)
            1 -> _allMaterials.filter { it.author == "Anda" }
            2 -> emptyList() // DOWNLOAD: Placeholder untuk materi yang diunduh
            else -> _allMaterials.toList()
        }

        // 2. FILTER BERDASARKAN PENCARIAN (diaplikasikan pada hasil filter tab)
        if (normalizedQuery.isNotBlank()) {
            filteredList = filteredList.filter { item ->
                item.title.lowercase().contains(normalizedQuery) ||
                        item.topic.lowercase().contains(normalizedQuery)
            }
        }

        _materials.value = filteredList
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
    // 5. UPLOAD FUNCTION (Diperbarui untuk memanggil filterMaterials)
    // ====================================================================
    fun uploadMaterial() {
        // ... (validasi)
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
            _allMaterials.add(0, newMaterial)

            // 2. Perbarui state _materials.
            // Setelah upload, kita paksa tampilkan tab ALL (indeks 0) tanpa query pencarian.
            filterMaterials("", 0)

            // Reset field input
            _subject.value = ""
            _topic.value = ""
            _description.value = ""
            _selectedFileUri.value = null

            _isLoading.value = false
            _uploadEvent.value = true
        }
    }
    // ====================================================================
    // 6. FUNGSI BARU: HAPUS MATERI
    // ====================================================================
    fun deleteMaterial(material: MaterialItem) {
        // Meluncurkan Coroutine di ViewModelScope
        viewModelScope.launch {
            // 1. Hapus materi dari daftar master (_allMaterials)
            _allMaterials.remove(material)

            // 2. Perbarui daftar materi yang ditampilkan dengan memanggil filter.
            filterMaterials("", 0)
        }
    }
}