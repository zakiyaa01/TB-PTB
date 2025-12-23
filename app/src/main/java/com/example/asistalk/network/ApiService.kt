package com.example.asistalk.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserProfile
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

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

interface ApiService {

    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

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
    @POST("api/materials")
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
        @Part("file_type") fileType: RequestBody, // Tambahkan ini agar sinkron
        @Part file: MultipartBody.Part? = null    // File bersifat opsional saat edit
    ): CommonResponse

    @DELETE("api/materials/{id}")
    suspend fun deleteMaterial(
        @Path("id") id: Int
    ): CommonResponse
}
