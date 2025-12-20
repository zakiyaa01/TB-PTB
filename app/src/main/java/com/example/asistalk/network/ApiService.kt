package com.example.asistalk.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

// =====================
// LOGIN
// =====================
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserProfile
)

// =====================
// REGISTER
// =====================
data class RegisterResponse(
    val success: Boolean,
    val message: String
)

// =====================
// PROFILE
// =====================
data class ProfileResponse(
    val success: Boolean,
    val data: UserProfile
)

data class UserProfile(
    val id: Int,
    val full_name: String,
    val username: String,
    val email: String,
    val phone_number: String,
    val birth_date: String,
    val gender: String,
    val profile_image: String
)

// =====================
// API SERVICE
// =====================
interface ApiService {

    @POST("api/auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): LoginResponse

    @Multipart
    @POST("api/auth/register")
    suspend fun registerUser(
        @Part("full_name") fullName: RequestBody,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_number") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part("birth_date") birthDate: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): RegisterResponse

    @GET("api/auth/profile/{id}")
    suspend fun getProfile(
        @Path("id") id: Int
    ): ProfileResponse

// POSTS
    @GET("api/posts")
    suspend fun getPosts(
        @Header("Authorization") token: String
    ): List<PostResponse>

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("content") content: RequestBody,
        @Part media: MultipartBody.Part?
    ): PostResponse

    // =====================
// COMMENTS
// =====================
    @GET("api/comments/{postId}")
    suspend fun getComments(
        @Path("postId") postId: Int
    ): List<CommentResponse>

    @POST("api/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Body body: CreateCommentRequest
    ): CommentResponse

}
