package com.example.asistalk.ui.asishub

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Definisikan model data untuk sebuah Post
data class Post(
    val id: Int, // ID unik untuk setiap post
    val author: String,
    val timestamp: String,
    val content: String,
    val commentsCount: Int = 0,
    val hasMedia: Boolean = false
)

// 2. Buat ViewModel untuk mengelola daftar post
class AsisHubViewModel : ViewModel() {

    // _posts adalah "wadah" internal yang bisa kita ubah.
    // MutableStateFlow adalah pilihan yang bagus untuk state yang sering berubah.
    private val _posts = MutableStateFlow<List<Post>>(emptyList())

    // posts adalah versi "read-only" yang akan diamati oleh UI.
    // Ini memastikan UI tidak bisa mengubah data secara langsung.
    val posts = _posts.asStateFlow()

    // Counter sederhana untuk ID post yang unik
    private var postIdCounter = 0

    // Fungsi untuk menambah post baru
    fun addPost(author: String, content: String) {
        val newPost = Post(
            id = postIdCounter++,
            author = author,
            timestamp = "Just now", // Bisa diganti dengan logic waktu sebenarnya
            content = content
        )

        // `update` adalah cara aman untuk mengubah state di StateFlow
        _posts.update { currentPosts ->
            // Menambahkan post baru di paling atas daftar
            listOf(newPost) + currentPosts
        }
    }

    // Fungsi untuk menghapus post berdasarkan ID-nya
    fun deletePost(postId: Int) {
        _posts.update { currentPosts ->
            currentPosts.filterNot { it.id == postId }
        }
    }

    // (Nanti bisa ditambahkan fungsi untuk edit post)
}