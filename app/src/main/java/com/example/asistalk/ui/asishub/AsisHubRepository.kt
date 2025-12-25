package com.example.asistalk.ui.asishub

import android.content.Context
import android.net.Uri
import com.example.asistalk.network.ApiService
import com.example.asistalk.network.CreateCommentRequest
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AsisHubRepository(
    private val api: ApiService,
    private val context: Context
) {

    suspend fun createPost(
        content: RequestBody,
        media: MultipartBody.Part?
    ) = api.createPost(
        content = content,
        media = media
    )

    fun prepareImagePart(uri: Uri): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()

        val requestBody = bytes.toRequestBody("image/*".toMediaType())

        return MultipartBody.Part.createFormData(
            name = "media",
            filename = "image.jpg",
            body = requestBody
        )
    }
    suspend fun createComment(
        postId: Int,
        comment: String
    ) = api.createComment(
        body = CreateCommentRequest(
            post_id = postId,
            comment = comment
        )
    )

    suspend fun updateComment(
        commentId: String,
        content: String
    ) = api.updateComment(
        id = commentId,
        body = mapOf("comment" to content)
    )

    suspend fun deleteComment(
        commentId: String
    ) = api.deleteComment(id = commentId)

    suspend fun getComments(postId: Int) =
        api.getComments(postId)

    suspend fun getPosts() =
        api.getPosts()

    suspend fun updatePost(
        postId: Int,
        content: RequestBody,
        media: MultipartBody.Part?
    ) = api.updatePost(
        id = postId.toString(),
        content = content,
        media = media
    )
    suspend fun deletePost(postId: Int) = api.deletePost(id = postId.toString())
}

