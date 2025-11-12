package com.example.asistalk.network

import com.google.gson.annotations.SerializedName

/**
 * Data class ini merepresentasikan struktur JSON yang dikembalikan oleh server
 * saat login berhasil atau gagal.
 *
 * Contoh JSON dari server:
 * {
 *   "success": true,
 *   "message": "Login berhasil!"
 * }
 */
data class LoginResponse(

    // @SerializedName("success") memberi tahu GSON untuk memetakan key "success" dari JSON
    // ke properti "success" di data class ini.
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?
)
