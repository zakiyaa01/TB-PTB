package com.example.asistalk.ui.asishub

import android.net.Uri // Pastikan import ini ada
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Definisikan model data untuk sebuah Post
//    Saya tambahkan 'imageUri' agar setiap post bisa punya gambar
data class Post(
    val id: Int,
    val author: String,
    val timestamp: String,
    val content: String,
    val imageUri: Uri? = null, // <-- TAMBAHAN: untuk menyimpan gambar postingan
    val commentsCount: Int = 0
)

// 2. Buat ViewModel untuk mengelola daftar post
class AsisHubViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    // --- PENAMBAHAN FUNGSI UNTUK GAMBAR ---

    // State untuk menyimpan URI gambar yang dipilih sementara saat membuat post
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    // Fungsi ini dipanggil saat pengguna memilih gambar (dari kamera atau galeri)
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    // Fungsi ini untuk membersihkan gambar yang dipilih,
    // misalnya saat postingan berhasil dibuat atau dibatalkan.
    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }

    // --- AKHIR PENAMBAHAN FUNGSI UNTUK GAMBAR ---

    private var postIdCounter = 0

    // Fungsi addPost sekarang juga menerima URI gambar
    fun addPost(author: String, content: String, imageUri: Uri?) {
        val newPost = Post(
            id = postIdCounter++,
            author = author,
            timestamp = "Just now",
            content = content,
            imageUri = imageUri // <-- Menyimpan URI gambar ke dalam data Post
        )

        _posts.update { currentPosts ->
            listOf(newPost) + currentPosts
        }

        // Setelah posting, bersihkan gambar yang dipilih
        clearSelectedImage()
    }

    fun deletePost(postId: Int) {
        _posts.update { currentPosts ->
            currentPosts.filterNot { it.id == postId }
        }
    }
}
