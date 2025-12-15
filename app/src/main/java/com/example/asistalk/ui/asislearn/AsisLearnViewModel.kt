package com.example.asistalk.ui.asislearn

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asistalk.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class yang digunakan secara konsisten oleh semua Composable
data class MaterialItem(
    val title: String, // Digunakan sebagai ID unik sementara (kunci)
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
            author = "Anda",
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
            author = "Anda",
            icon = R.drawable.ic_image,
            topic = "Diagram UML Proyek",
            description = "Dokumen yang berisi diagram dan arsitektur proyek."
        ),
    )

    private val _materials = MutableStateFlow(_allMaterials.toList())
    val materials = _materials.asStateFlow()

    private val _selectedMaterial = MutableStateFlow<MaterialItem?>(null)
    val selectedMaterial = _selectedMaterial.asStateFlow()

    fun getMaterialByTitle(title: String) {
        val material = _materials.value.find { it.title == title }
        _selectedMaterial.value = material
    }

    // ====================================================================
    // 2. FUNGSI FILTERING BARU
    // ====================================================================

    fun filterMaterials(query: String, selectedTab: Int) {
        val normalizedQuery = query.trim().lowercase()
        var filteredList: List<MaterialItem> = _allMaterials

        // 1. FILTER BERDASARKAN TAB
        filteredList = when (selectedTab) {
            0 -> _allMaterials.toList()
            1 -> _allMaterials.filter { it.author == "Anda" }
            2 -> emptyList()
            else -> _allMaterials.toList()
        }

        // 2. FILTER BERDASARKAN PENCARIAN
        if (normalizedQuery.isNotBlank()) {
            filteredList = filteredList.filter { item ->
                item.title.lowercase().contains(normalizedQuery) ||
                        item.topic.lowercase().contains(normalizedQuery)
            }
        }

        _materials.value = filteredList
    }

    // ====================================================================
    // 3. STATE INPUT UPLOAD/EDIT
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
    // 4. STATE KHUSUS EDIT BARU
    // ====================================================================

    private val _currentFileName = MutableStateFlow("")
    val currentFileName: StateFlow<String> = _currentFileName.asStateFlow()

    private val _editEvent = MutableStateFlow<Boolean?>(null)
    val editEvent: StateFlow<Boolean?> = _editEvent.asStateFlow()

    private var originalMaterialTitle: String? = null

    // ====================================================================
    // 5. UPDATE STATE FUNCTIONS
    // ====================================================================

    fun onSubjectChange(value: String) { _subject.value = value }
    fun onTopicChange(value: String) { _topic.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onFileTypeChange(value: String) { _fileType.value = value }
    fun onFileSelected(uri: Uri?) { _selectedFileUri.value = uri }
    fun consumeUploadEvent() { _uploadEvent.value = null }
    fun consumeEditEvent() { _editEvent.value = null }

    // ====================================================================
    // 6. FUNGSI BARU: RESET INPUT STATES (FIX 2)
    // ====================================================================
    /**
     * Mereset semua field input form (dipanggil setelah upload/edit selesai,
     * atau saat membuka form upload).
     */
    fun resetInputStates() {
        _subject.value = ""
        _topic.value = ""
        _description.value = ""
        _fileType.value = "PDF" // Set ke nilai default
        _selectedFileUri.value = null
        _currentFileName.value = ""
        originalMaterialTitle = null
        _isLoading.value = false
    }

    // ====================================================================
    // 7. FUNGSI EDIT (DIPERBAIKI: Panggil resetInputStates)
    // ====================================================================

    fun loadMaterialForEdit(materialId: String) {
        // Penting: Jangan panggil resetInputStates() di sini, karena ini adalah fungsi load.
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)

            val materialToEdit = _allMaterials.find { it.title == materialId }

            if (materialToEdit != null) {
                originalMaterialTitle = materialToEdit.title
                _subject.value = materialToEdit.title
                _topic.value = materialToEdit.topic
                _description.value = materialToEdit.description
                _fileType.value = materialToEdit.type
                _currentFileName.value = materialToEdit.fileUri.substringAfterLast("/")
                _selectedFileUri.value = null
            } else {
                originalMaterialTitle = null
            }

            _isLoading.value = false
        }
    }

    fun updateMaterial(materialId: String) {
        if (_subject.value.isBlank() || _topic.value.isBlank()) {
            _editEvent.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // SIMULASI UPDATE API

            val materialIndex = _allMaterials.indexOfFirst { it.title == materialId }

            if (materialIndex != -1) {
                val existingMaterial = _allMaterials[materialIndex]
                val newFileUri = _selectedFileUri.value?.toString() ?: existingMaterial.fileUri
                val iconRes = when (_fileType.value.lowercase()) {
                    "pdf" -> R.drawable.ic_pdf
                    "video" -> R.drawable.ic_video
                    "image" -> R.drawable.ic_image
                    else -> R.drawable.ic_pdf
                }

                val updatedMaterial = existingMaterial.copy(
                    title = _subject.value,
                    type = _fileType.value,
                    topic = _topic.value,
                    description = _description.value,
                    fileUri = newFileUri,
                    icon = iconRes
                )

                _allMaterials[materialIndex] = updatedMaterial
                filterMaterials("", 0)

                // --- FIX 1 & 2: Reset state setelah berhasil update ---
                resetInputStates()
                _editEvent.value = true // Sukses!
            } else {
                _editEvent.value = false
            }

            _isLoading.value = false
        }
    }

    // ====================================================================
    // 8. UPLOAD FUNCTION (DIPERBAIKI: Panggil resetInputStates)
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

            _allMaterials.add(0, newMaterial)
            filterMaterials("", 0)

            // --- FIX 1 & 2: Reset state setelah berhasil upload ---
            resetInputStates()

            _isLoading.value = false
            _uploadEvent.value = true
        }
    }

    // ====================================================================
    // 9. FUNGSI HAPUS MATERI
    // ====================================================================
    fun deleteMaterial(material: MaterialItem) {
        viewModelScope.launch {
            delay(500)

            _allMaterials.remove(material)
            filterMaterials("", 0)
        }
    }
}