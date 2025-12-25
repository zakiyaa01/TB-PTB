package com.example.asistalk.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
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
    val user: UserProfile
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

data class CommonResponse(
    val success: Boolean,
    val message: String
)

data class MaterialResponse(
    val success: Boolean,
    val data: List<MaterialItem>
)

data class MaterialItem(
    val id: Int,
    val user_id: Int,
    val subject: String,
    val topic: String,
    val description: String?,
    val file_type: String,
    val file_path: String,
    val author_name: String,
    val profile_image: String?
)

// =====================
// ASISHUB - POSTS
// =====================
data class CreatePostResponse(
    val success: Boolean,
    val post: PostResponse
)
data class PostResponse(
    val id: Int,
    val content: String,
    val media: String?,
    val media_type: String?,
    val created_at: String,
    val user_id: Int,
    val username: String,
    val profile_image: String?
)
// =====================
// ASISHUB - COMMENT
// =====================
data class CreateCommentRequest(
    val post_id: Int,
    val comment: String
)
data class CommentResponse(
    val id: Int,
    val comment: String,
    val created_at: String,
    val username: String,
    val profile_image: String?
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
    suspend fun getProfile(@Path("id") id: Int): ProfileResponse

    // =====================
    // ASISLEARN MATERIALS
    // =====================
    @GET("api/materials")
    suspend fun getAllMaterials(): MaterialResponse

    @Multipart
    @POST("api/materials/")
    suspend fun uploadMaterial(
        @Part("subject") subject: RequestBody,
        @Part("topic") topic: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("file_type") fileType: RequestBody,
        @Part file: MultipartBody.Part
    ): CommonResponse

    @Multipart
    @PUT("api/materials/{id}")
    suspend fun updateMaterial(
        @Path("id") id: Int,
        @Part("subject") subject: RequestBody,
        @Part("topic") topic: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("file_type") fileType: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): CommonResponse

    @DELETE("api/materials/{id}")
    suspend fun deleteMaterial(
        @Path("id") id: Int
    ): CommonResponse

    // =====================
    // POSTS
    // =====================
    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Part("content") content: RequestBody,
        @Part media: MultipartBody.Part?
    ): CreatePostResponse

    @GET("api/posts")
    suspend fun getPosts(): List<PostResponse>

    @Multipart
    @PUT("api/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: String,
        @Part("content") content: RequestBody,
        @Part media: MultipartBody.Part?
    ): CreatePostResponse

    @DELETE("api/posts/{id}")
    suspend fun deletePost(
        @Path("id") id: String
    )

    // =====================
    // COMMENTS
    // =====================
    @POST("api/comments")
    suspend fun createComment(
        @Body body: CreateCommentRequest
    )

    @GET("api/comments/{postId}")
    suspend fun getComments(
        @Path("postId") postId: Int
    ): List<CommentResponse>

    @PUT("api/comments/{id}")
    suspend fun updateComment(
        @Path("id") id: String,
        @Body body: Map<String, String>
    )

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(
        @Path("id") id: String
    )
} 