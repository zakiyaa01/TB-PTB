package com.example.asistalk.ui.profile

import com.example.asistalk.network.ApiService

class ProfileRepository(
    private val api: ApiService
) {
    suspend fun getProfile(userId: Int) =
        api.getProfile(userId)
}
