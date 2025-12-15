package com.example.asistalk.network

import retrofit2.http.Body
import retrofit2.http.POST

// --- Data Classes ---

// Data yang dikirim ke server saat login
data class LoginRequest(
    val username: String,
    val password: String
)

// data class LoginResponse sekarang dihapus dari sini karena sudah ada di filenya sendiri (LoginResponse.kt)

// Data yang dikirim ke server saat register
data class RegisterRequest(
    val full_name: String,
    val username: String,
    val email: String,
    val password: String
)

// Data yang diterima dari server setelah register
data class RegisterResponse(
    val success: Boolean,
    val message: String?
)


// --- Retrofit Service Interface ---
interface ApiService {
    /**
     * Mengirimkan data login ke server dan menerima respons.
     * @param request Objek yang berisi username dan password.
     * @return Objek LoginResponse dari server.
     */
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    /**
     * Mengirimkan data registrasi ke server dan menerima respons.
     * @param request Objek yang berisi data user baru.
     * @return Objek RegisterResponse dari server.
     */
    @POST("auth/register") // Pastikan endpoint "register" ini sesuai dengan API Anda
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): RegisterResponse
}