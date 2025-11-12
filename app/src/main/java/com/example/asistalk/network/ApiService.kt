package com.example.asistalk.network

import retrofit2.http.Body
import retrofit2.http.POST

// Data yang dikirim ke server
data class LoginRequest(
    val username: String,
    val password: String
)

// Interface Retrofit untuk endpoint /login
interface ApiService {
    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse
}
