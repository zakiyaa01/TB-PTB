package com.example.asistalk.ui.asishub

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// =======================================================================
// Perubahan #1: Tambahkan data class untuk Comment
// =======================================================================
data class Comment(
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String
)

// =======================================================================
// Perubahan #2: Modifikasi data class Post
// =======================================================================
data class Post(
    val id: Int,
    val author: String,
    val timestamp: String,
    val content: String,
    val imageUri: Uri? = null,
    // Hapus 'commentsCount', ganti dengan list 'comments'
    val comments: List<Comment> = emptyList()
) {
    // Properti ini akan menghitung jumlah komentar secara otomatis
    val commentsCount: Int
        get() = comments.size
}


// Buat ViewModel untuk mengelola daftar post
class AsisHubViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // State untuk menyimpan URI gambar yang dipilih sementara (Tidak ada perubahan)
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    // State untuk menampung post yang sedang diedit (Tidak ada perubahan)
    private val _postToEdit = MutableStateFlow<Post?>(null)
    val postToEdit: StateFlow<Post?> = _postToEdit.asStateFlow()

    // =======================================================================
    // Perubahan #3: Tambahkan state dan fungsi baru untuk fitur komentar
    // =======================================================================

    // State untuk menyimpan post yang akan ditampilkan di halaman detail
    private val _postToView = MutableStateFlow<Post?>(null)
    val postToView: StateFlow<Post?> = _postToView

    /**
     * Menyiapkan post yang akan dilihat detail dan komentarnya.
     * Dipanggil dari AsisHubScreen sebelum navigasi ke PostDetailScreen.
     */
    fun selectPostForViewing(post: Post) {
        _postToView.value = post
    }

    /**
     * Menambahkan komentar baru ke sebuah post.
     * Dipanggil dari PostDetailScreen saat tombol kirim ditekan.
     */
    fun addComment(postId: Int, commentContent: String) {
        // Cari post di daftar _posts dan tambahkan komentar baru
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    val newComment = Comment(
                        id = "comment_${System.currentTimeMillis()}",
                        author = "Zakiya Aulia", // Nanti bisa diganti dengan user yang login
                        content = commentContent,
                        timestamp = "Just now"
                    )
                    // Salin post lama, lalu tambahkan komentar baru ke daftarnya
                    post.copy(comments = post.comments + newComment)
                } else {
                    post // Biarkan post lain apa adanya
                }
            }
        }
        // Update juga post yang sedang ditampilkan di halaman detail agar UI langsung refresh
        _postToView.value = _posts.value.find { it.id == postId }
    }

// =======================================================================
// Perubahan #4: Tambahkan fungsi untuk mengelola komentar
// =======================================================================

    /**
     * Menghapus sebuah komentar dari sebuah post.
     */
    fun deleteComment(postId: Int, commentId: String) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    // Buat daftar komentar baru tanpa komentar yang akan dihapus
                    val updatedComments = post.comments.filterNot { it.id == commentId }
                    post.copy(comments = updatedComments)
                } else {
                    post
                }
            }
        }
        // Perbarui juga post yang sedang dilihat agar UI langsung refresh
        _postToView.value = _posts.value.find { it.id == postId }
    }

    /**
     * Mengedit konten dari sebuah komentar.
     */
    fun updateComment(postId: Int, commentId: String, newContent: String) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    // Cari komentar yang akan diedit dan perbarui kontennya
                    val updatedComments = post.comments.map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(content = newContent) // Perbarui kontennya
                        } else {
                            comment
                        }
                    }
                    post.copy(comments = updatedComments)
                } else {
                    post
                }
            }
        }
        // Perbarui juga post yang sedang dilihat
        _postToView.value = _posts.value.find { it.id == postId }
    }

    // =======================================================================
    // Di bawah ini adalah kode Anda yang sudah ada sebelumnya (Tidak ada perubahan)
    // =======================================================================

    // FUNGSI UNTUK GAMBAR
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }

    // FUNGSI CRUD UNTUK POST
    private var postIdCounter = 0

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

    fun deletePost(postId: Int) {
        _posts.update { currentPosts -> currentPosts.filterNot { it.id == postId } }
    }

    // FUNGSI UNTUK EDIT POST
    fun selectPostForEditing(post: Post) {
        _postToEdit.value = post
        onImageSelected(post.imageUri)
    }

    fun updatePost(updatedContent: String, newImageUri: Uri?) {
        val originalPost = _postToEdit.value ?: return
        val updatedPost = originalPost.copy(
            content = updatedContent,
            imageUri = newImageUri,
            timestamp = "Edited"
        )
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == originalPost.id) {
                    updatedPost
                } else {
                    post
                }
            }
        }
        clearEditingState()
    }

    fun clearEditingState() {
        _postToEdit.value = null
        clearSelectedImage()
    }
}