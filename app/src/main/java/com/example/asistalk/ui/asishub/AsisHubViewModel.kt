package com.example.asistalk.ui.asishub

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.asistalk.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class Comment(
    val id: String,
    val author: String,
    val profileImage: String? = null,
    val content: String,
    val timestamp: String
)

data class Post(
    val id: Int,
    val author: String,
    val authorProfileImage: String? = null,
    val timestamp: String,
    val content: String,
    val imageUri: Uri? = null,
    val comments: List<Comment> = emptyList()
)

class AsisHubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AsisHubRepository(
        api = RetrofitClient.getInstance(getApplication()),
        context = getApplication()
    )
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private val _postToEdit = MutableStateFlow<Post?>(null)
    val postToEdit: StateFlow<Post?> = _postToEdit
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri
    private val _postToView = MutableStateFlow<Post?>(null)
    val postToView: StateFlow<Post?> = _postToView
    private val _currentUserProfile = MutableStateFlow<String?>(null)
    val currentUserProfile: StateFlow<String?> = _currentUserProfile


    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }


    fun createPost(content: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imagePart: MultipartBody.Part? =
                    imageUri?.let { repository.prepareImagePart(it) }

                val response = repository.createPost(
                    content = content.toRequestBody("text/plain".toMediaType()),
                    media = imagePart
                )

                val postFromApi = response.post

                val newPost = Post(
                    id = postFromApi.id,
                    author = postFromApi.username,
                    timestamp = postFromApi.created_at.split("T")[0],
                    content = postFromApi.content,
                    imageUri = postFromApi.media?.let { Uri.parse(it) },
                    comments = emptyList()
                )

                _posts.value = listOf(newPost) + _posts.value
                clearSelectedImage()

            } catch (e: Exception) {
                Log.e("CREATE_POST", e.message ?: "Create post failed")
            }
        }
    }

    fun selectPostForViewing(post: Post) {
        _postToView.value = post
        loadComments(post.id)
    }

    fun addComment(postId: Int, content: String) {
        viewModelScope.launch {
            try {
                repository.createComment(
                    postId = postId,
                    comment = content
                )
               loadComments(postId)
            } catch (e: Exception) {
                Log.e("ADD_COMMENT", e.message ?: "Gagal")
            }
        }
    }

    fun loadComments(postId: Int) {
        viewModelScope.launch {
            try {
                val comments = repository.getComments(postId)
                val mapped = comments.map {
                    Comment(
                        id = it.id.toString(),
                        author = it.username,
                        profileImage = it.profile_image,
                        content = it.comment,
                        timestamp = it.created_at.split("T")[0]
                    )
                }

                _posts.update { list ->
                    list.map { p -> if (p.id == postId) p.copy(comments = mapped) else p }
                }

                _postToView.value = _posts.value.find { it.id == postId }

            } catch (e: Exception) {
                Log.e("LOAD_COMMENT", "${e.message}")
            }
        }
    }

    fun updateComment(postId: Int, commentId: String, newText: String) {
        viewModelScope.launch {
            try {
                repository.updateComment(commentId = commentId, content = newText)
                loadComments(postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteComment(postId: Int, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(commentId = commentId)
                loadComments(postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectPostForEditing(post: Post) {
        _postToEdit.value = post
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)

                _posts.update { list ->
                    list.filterNot { it.id == postId }
                }

                if (_postToView.value?.id == postId) {
                    _postToView.value = null
                }
            } catch (e: Exception) {
                Log.e("DELETE_POST", "Gagal menghapus: ${e.message}")
            }
        }
    }

    fun clearEditingState() {
        _postToEdit.value = null
        clearSelectedImage()
    }

    fun updatePost(updatedContent: String, newImageUri: Uri?) {
        val post = _postToEdit.value ?: return
        viewModelScope.launch {
            try {
                repository.updatePost(
                    postId = post.id,
                    content = updatedContent.toRequestBody("text/plain".toMediaType()),
                    media = null
                )

                _posts.update { list ->
                    list.map {
                        if (it.id == post.id) it.copy(content = updatedContent, imageUri = newImageUri)
                        else it
                    }
                }

                _postToView.value = _posts.value.find { it.id == post.id }

                fetchPosts()

                clearEditingState()
            } catch (e: Exception) {
                Log.e("UPDATE_ERROR", "${e.message}")
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val response = repository.getPosts()
                if (response.isNotEmpty()) {
                    _currentUserProfile.value = response.firstOrNull()?.profile_image
                }

                val mappedPosts = response.map {
                    Post(
                        id = it.id,
                        author = it.username,
                        authorProfileImage = it.profile_image,
                        timestamp = it.created_at.split("T")[0],
                        content = it.content,
                        imageUri = it.media?.let { media ->
                            Uri.parse(RetrofitClient.BASE_IMAGE_URL + media)
                        },
                        comments = emptyList()
                    )
                }

                _posts.value = mappedPosts

            } catch (e: Exception) {
                Log.e("FETCH_POSTS", e.message ?: "Failed fetch posts")
            }
        }
    }
}