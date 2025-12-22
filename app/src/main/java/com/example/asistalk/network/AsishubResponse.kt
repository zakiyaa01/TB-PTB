package com.example.asistalk.network

data class CommentResponse(
    val id: Int,
    val comment: String,
    val created_at: String,
    val username: String
)
data class PostResponse(
    val id: Int,
    val content: String,
    val media: String?,
    val created_at: String,
    val username: String,
    val profile_image: String?
)
data class CreateCommentRequest(
    val post_id: Int,
    val comment: String
)