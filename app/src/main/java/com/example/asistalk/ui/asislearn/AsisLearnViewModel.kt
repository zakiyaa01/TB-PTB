package com.example.asistalk.ui.asislearn

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
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

class AsisLearnViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.getInstance(application.applicationContext)
    private val repository = AsisLearnRepository(apiService)

    // --- DATA STATE ---
    private val _allMaterials = MutableStateFlow<List<MaterialItem>>(emptyList())
    private val _materials = MutableStateFlow<List<MaterialItem>>(emptyList())
    val materials = _materials.asStateFlow()

    private val _selectedMaterial = MutableStateFlow<MaterialItem?>(null)
    val selectedMaterial = _selectedMaterial.asStateFlow()

    // State untuk menyimpan ID materi yang sudah ada di folder Download lokal
    private val _downloadedIds = MutableStateFlow<Set<Int>>(emptySet())
    val downloadedIds = _downloadedIds.asStateFlow()

    // --- FORM & UI STATE ---
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

    // --- SESSION & FILTER STATE ---
    var currentUserId by mutableStateOf(-1)
    var currentUserFullName by mutableStateOf("")
    var selectedTabIndex by mutableIntStateOf(0)
    var searchQuery by mutableStateOf("")
    var editingMaterialId: Int? = null

    fun setSession(id: Int, fullName: String) {
        currentUserId = id
        currentUserFullName = fullName
    }

    // --- FETCH DATA ---
    fun fetchAllMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAllMaterials()
                if (response.success) {
                    _allMaterials.value = response.data
                    filterMaterials()
                }
            } catch (e: Exception) {
                Log.e("AsisLearnVM", "Fetch Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- LOGIKA FILTER (Termasuk Tab Download) ---
    fun filterMaterials() {
        var filtered = _allMaterials.value

        when (selectedTabIndex) {
            1 -> filtered = filtered.filter { it.user_id == currentUserId }
            2 -> filtered = filtered.filter { downloadedIds.value.contains(it.id) }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.subject.contains(searchQuery, ignoreCase = true) ||
                        it.topic.contains(searchQuery, ignoreCase = true)
            }
        }
        _materials.value = filtered
    }

    // --- FUNGSI DOWNLOAD ---
    fun downloadMaterial(context: Context, url: String, subject: String) {
        try {
            val adjustedUrl = url.replace("localhost", "10.0.2.2")
            val fileName = "${subject.replace(" ", "_")}.pdf"

            val request = DownloadManager.Request(Uri.parse(adjustedUrl))
                .setTitle("Mengunduh Materi")
                .setDescription(subject)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(context, "Unduhan dimulai...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("DownloadError", e.message.toString())
            Toast.makeText(context, "Gagal mengunduh", Toast.LENGTH_SHORT).show()
        }
    }

    // --- FUNGSI CEK FILE LOKAL (Untuk Tab Download) ---
    fun checkDownloadedMaterials(context: Context, allMaterials: List<MaterialItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val downloadedSet = mutableSetOf<Int>()
            allMaterials.forEach { item ->
                val fileName = "${item.subject.replace(" ", "_")}.pdf"
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                if (file.exists()) {
                    downloadedSet.add(item.id)
                }
            }
            _downloadedIds.value = downloadedSet
            withContext(Dispatchers.Main) {
                filterMaterials()
            }
        }
    }

    // --- ACTION METHODS ---
    fun onSubjectChange(v: String) { _subject.value = v }
    fun onTopicChange(v: String) { _topic.value = v }
    fun onDescriptionChange(v: String) { _description.value = v }
    fun onFileTypeChange(v: String) { _fileType.value = v }

    fun onFileSelected(uri: Uri?, context: Context) {
        _selectedFileUri.value = uri
        _currentFileName.value = uri?.let { getFileName(it, context) } ?: ""
    }

    fun getDetailFromList(id: Int) {
        _selectedMaterial.value = _allMaterials.value.find { it.id == id }
    }

    fun setEditData(item: MaterialItem) {
        editingMaterialId = item.id
        _subject.value = item.subject
        _topic.value = item.topic
        _description.value = item.description ?: ""
        _fileType.value = item.file_type
        _currentFileName.value = item.file_path.substringAfterLast("/")
    }

    // --- UPLOAD & UPDATE MATERIAL (Perbaikan file path) ---
    fun uploadMaterial(context: Context) {
        val uri = _selectedFileUri.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val file = withContext(Dispatchers.IO) { uriToFile(uri, context) }
                file?.let {
                    val originalName = _currentFileName.value.ifEmpty { it.name }
                    val response = repository.uploadMaterial(
                        createPart(_subject.value),
                        createPart(_topic.value),
                        createPart(_description.value),
                        createPart(_fileType.value),
                        createMultipart(it, originalName)
                    )
                    _uploadEvent.value = response.success
                    if (response.success) fetchAllMaterials()
                }
            } catch (e: Exception) {
                _uploadEvent.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMaterial(context: Context) {
        val id = editingMaterialId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var filePart: MultipartBody.Part? = null
                _selectedFileUri.value?.let { uri ->
                    val file = withContext(Dispatchers.IO) { uriToFile(uri, context) }
                    file?.let {
                        val originalName = _currentFileName.value.ifEmpty { it.name }
                        filePart = createMultipart(it, originalName)
                    }
                }

                val response = repository.updateMaterial(
                    id,
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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMaterial(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteMaterial(id)
                if (response.success) {
                    _allMaterials.value = _allMaterials.value.filter { it.id != id }
                    filterMaterials()
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

    // --- HELPERS ---
    private fun createPart(v: String): RequestBody = v.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createMultipart(f: File, originalFileName: String): MultipartBody.Part {
        val requestFile = f.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", originalFileName, requestFile)
    }

    private fun getFileName(uri: Uri, context: Context): String {
        var name = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                name = cursor.getString(index)
            }
        }
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