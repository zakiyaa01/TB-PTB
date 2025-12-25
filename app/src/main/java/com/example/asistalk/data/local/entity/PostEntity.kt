package com.example.asistalk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val content: String,
    val media: String?,
    val created_at: String,
    val username: String,
    val profile_image: String?
)
