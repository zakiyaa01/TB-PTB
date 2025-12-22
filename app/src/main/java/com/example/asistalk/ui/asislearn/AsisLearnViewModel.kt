package com.example.asistalk.ui.asislearn

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asistalk.network.MaterialItem
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AsisLearnViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    // Master data agar filter tidak perlu hit API terus menerus
    private val _allMaterials = MutableStateFlow<List<MaterialItem>>(emptyList())

    // Data yang ditampilkan ke UI (setelah filter)
    private val _materials = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materials = _materials.asStateFlow()

    // Form States
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
    private val _currentFileName = MutableStateFlow("")
    val currentFileName = _currentFileName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uploadEvent = MutableStateFlow<Boolean?>(null)
    val uploadEvent = _uploadEvent.asStateFlow()

    // --- DATA USER LOGIN (Dinamis dari Session) ---
    var currentUserId by mutableStateOf(-1)
    var currentUserToken by mutableStateOf("")

    var selectedTabIndex by mutableStateOf(0)
    var searchQuery by mutableStateOf("")

    // ✅ KEMBALI: Fungsi sapaan nama lengkap di Home
    var currentUserFullName by mutableStateOf("")

    var editingMaterialId: Int? = null

    /**
     * FUNGSI SESSI: Mengisi identitas user lengkap
     */
    fun setSession(id: Int, token: String, fullName: String) {
        currentUserId = id
        currentUserToken = token
        currentUserFullName = fullName // Menyimpan nama untuk sapaan di Home
        Log.d("AsisLearnVM", "Session Updated - User: $fullName (ID: $id)")
    }

    fun fetchAllMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getAllMaterials()
                if (response.success) {
                    _allMaterials.value = response.data
                    filterMaterials() // filter sesuai tab & search saat ini
                }
            } catch (e: Exception) {
                Log.e("AsisLearnVM", "Fetch Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterMaterials() {
        var filtered = _allMaterials.value

        filtered = when (selectedTabIndex) {
            1 -> filtered.filter { it.user_id == currentUserId } // My Material
            else -> filtered
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.subject.contains(searchQuery, ignoreCase = true) ||
                        it.topic.contains(searchQuery, ignoreCase = true)
            }
        }

        _materials.value = filtered
    }


    // --- Action Methods ---
    fun onSubjectChange(v: String) { _subject.value = v }
    fun onTopicChange(v: String) { _topic.value = v }
    fun onDescriptionChange(v: String) { _description.value = v }
    fun onFileTypeChange(v: String) { _fileType.value = v }

    fun onFileSelected(uri: Uri?, context: Context) {
        _selectedFileUri.value = uri
        _currentFileName.value = uri?.let { getFileName(it, context) } ?: ""
    }

    fun setEditData(item: MaterialItem) {
        editingMaterialId = item.id
        _subject.value = item.subject
        _topic.value = item.topic
        _description.value = item.description ?: ""
        _fileType.value = item.file_type
        _currentFileName.value = item.file_path.substringAfterLast("/")
    }

    fun uploadMaterial(context: Context, token: String) {
        val uri = _selectedFileUri.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val file = withContext(Dispatchers.IO) { uriToFile(uri, context) }
                file?.let {
                    val response = apiService.uploadMaterial(
                        "Bearer $token",
                        createPart(_subject.value),
                        createPart(_topic.value),
                        createPart(_description.value),
                        createPart(_fileType.value),
                        createMultipart(it)
                    )
                    _uploadEvent.value = response.success
                    if (response.success) fetchAllMaterials()
                }
            } catch (e: Exception) {
                Log.e("UploadError", e.message.toString())
                _uploadEvent.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMaterial(context: Context, token: String) {
        val id = editingMaterialId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var filePart: MultipartBody.Part? = null
                _selectedFileUri.value?.let { uri ->
                    val file = withContext(Dispatchers.IO) { uriToFile(uri, context) }
                    file?.let { filePart = createMultipart(it) }
                }

                val response = apiService.updateMaterial(
                    "Bearer $token", id,
                    createPart(_subject.value),
                    createPart(_topic.value),
                    createPart(_description.value),
                    createPart(_fileType.value),
                    filePart
                )
                _uploadEvent.value = response.success
                if (response.success) fetchAllMaterials()
            } catch (e: Exception) {
                _uploadEvent.value = false
                Log.e("UpdateError", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMaterial(id: Int, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteMaterial("Bearer $token", id)
                if (response.success) {
                    // 1. Hapus item dari _allMaterials
                    _allMaterials.value = _allMaterials.value.filter { it.id != id }
                    // 2. Filter ulang supaya UI ter-update
                    filterMaterials()
                } else {
                    Log.e("DeleteError", "Gagal hapus: response.success=false")
                }
            } catch (e: Exception) {
                Log.e("DeleteError", "Gagal hapus: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun consumeUploadEvent() { _uploadEvent.value = null }

    fun resetInputStates() {
        _subject.value = ""
        _topic.value = ""
        _description.value = ""
        _fileType.value = "PDF"
        _selectedFileUri.value = null
        _currentFileName.value = ""
        editingMaterialId = null
    }

    // --- Helpers ---
    private fun createPart(v: String): RequestBody = v.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createMultipart(f: File): MultipartBody.Part {
        val requestFile = f.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", f.name, requestFile)
    }

    private fun getFileName(uri: Uri, context: Context): String {
        var name = ""
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    name = it.getString(index)
                }
            }
        } catch (e: Exception) { name = "" }
        return name.ifEmpty { uri.path?.substringAfterLast('/') ?: "file" }
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file
        } catch (e: Exception) { null }
    }
}