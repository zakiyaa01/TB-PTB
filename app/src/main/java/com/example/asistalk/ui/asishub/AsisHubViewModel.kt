package com.example.asistalk.ui.asishub

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Definisikan model data untuk sebuah Post (Tidak ada perubahan)
data class Post(
    val id: Int,
    val author: String,
    val timestamp: String,
    val content: String,
    val imageUri: Uri? = null,
    val commentsCount: Int = 0
)

// 2. Buat ViewModel untuk mengelola daftar post
class AsisHubViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // State untuk menyimpan URI gambar yang dipilih sementara (Tidak ada perubahan)
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    // ✅ State untuk menampung post yang sedang diedit
    private val _postToEdit = MutableStateFlow<Post?>(null)
    val postToEdit: StateFlow<Post?> = _postToEdit.asStateFlow()


    // --- FUNGSI UNTUK GAMBAR (Tidak ada perubahan) ---
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }

    // --- FUNGSI CRUD UNTUK POST ---

    private var postIdCounter = 0

    // Fungsi addPost (Tidak ada perubahan)
    fun addPost(author: String, content: String, imageUri: Uri?) {
        val newPost = Post(
            id = postIdCounter++,
            author = author,
            timestamp = "Just now",
            content = content,
            imageUri = imageUri
        )
        _posts.update { currentPosts -> listOf(newPost) + currentPosts }
        clearSelectedImage()
    }

    // Fungsi deletePost (Tidak ada perubahan)
    fun deletePost(postId: Int) {
        _posts.update { currentPosts -> currentPosts.filterNot { it.id == postId } }
    }


    // ✅ --- FUNGSI BARU UNTUK EDIT POST ---

    /**
     * Menyiapkan post yang akan diedit.
     * Fungsi ini akan dipanggil dari AsisHubScreen sebelum navigasi.
     */
    fun selectPostForEditing(post: Post) {
        _postToEdit.value = post
        // Saat memilih post, langsung set gambarnya (jika ada) ke state gambar terpilih
        // agar EditPostScreen bisa langsung menampilkannya.
        onImageSelected(post.imageUri)
    }

    /**
     * Memperbarui postingan yang ada dengan konten dan/atau gambar baru.
     * Fungsi ini akan dipanggil dari EditPostScreen saat tombol 'Simpan' ditekan.
     */
    fun updatePost(updatedContent: String, newImageUri: Uri?) {
        // Ambil post asli yang sedang kita edit dari state
        val originalPost = _postToEdit.value ?: return

        // Buat objek post yang sudah diperbarui menggunakan .copy()
        val updatedPost = originalPost.copy(
            content = updatedContent,
            imageUri = newImageUri,
            timestamp = "Edited" // Kita bisa tambahkan penanda bahwa post ini sudah diedit
        )

        // Cari post lama di dalam daftar `_posts` dan ganti dengan yang baru
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == originalPost.id) {
                    updatedPost // Ini post yang kita edit, ganti dengan versi baru
                } else {
                    post // Ini post lain, biarkan apa adanya
                }
            }
        }

        // Setelah selesai, bersihkan state editing
        clearEditingState()
    }

    /**
     * Membersihkan state editing setelah selesai, dibatalkan, atau kembali.
     */
    fun clearEditingState() {
        _postToEdit.value = null
        clearSelectedImage() // Sekalian bersihkan juga gambar yang dipilih
    }
}

