package com.example.asistalk.ui.asislearn

import com.example.asistalk.network.ApiService
import com.example.asistalk.network.CommonResponse
import com.example.asistalk.network.MaterialResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AsisLearnRepository(private val apiService: ApiService) {

    suspend fun getAllMaterials(): MaterialResponse = apiService.getAllMaterials()

    suspend fun uploadMaterial(
        subject: RequestBody,
        topic: RequestBody,
        description: RequestBody?,
        fileType: RequestBody,
        file: MultipartBody.Part
    ): CommonResponse {
        return apiService.uploadMaterial(subject, topic, description, fileType, file)
    }

    suspend fun updateMaterial(
        id: Int,
        subject: RequestBody,
        topic: RequestBody,
        description: RequestBody?,
        fileType: RequestBody,
        file: MultipartBody.Part?
    ): CommonResponse {
        return apiService.updateMaterial(id, subject, topic, description, fileType, file)
    }

    suspend fun deleteMaterial(id: Int): CommonResponse {
        return apiService.deleteMaterial(id)
    }
}