package com.example.asistalk.utils // Pastikan package-nya sudah benar

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ComposeFileProvider {
    fun getImageUri(context: Context): Uri {
        val directory = File(context.cacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile(
            "selected_image_",
            ".jpg",
            directory
        )
        // Pastikan authority cocok dengan yang ada di AndroidManifest.xml
        val authority = "com.example.asistalk.provider" // Langsung tulis nama package Anda
        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }
}
    