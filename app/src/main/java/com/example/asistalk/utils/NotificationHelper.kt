package com.example.asistalk.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Model data untuk menyimpan info notifikasi
data class NotificationLog(
    val title: String,
    val message: String,
    val time: Long = System.currentTimeMillis()
)

object NotificationHelper {
    private const val PREF_NAME = "asis_notif_prefs"
    private const val KEY_LOGS = "notification_logs"

    // Fungsi untuk menambah log baru (Upload, Download, Hapus, dll)
    fun addLog(context: Context, title: String, message: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()

        // Ambil list lama, tambah yang baru di posisi paling atas (index 0)
        val currentLogs = getLogs(context).toMutableList()
        currentLogs.add(0, NotificationLog(title, message))

        // Batasi maksimal 20 notifikasi saja supaya tidak berat
        val limitedLogs = if (currentLogs.size > 20) currentLogs.take(20) else currentLogs

        prefs.edit().putString(KEY_LOGS, gson.toJson(limitedLogs)).apply()
    }

    // Fungsi untuk mengambil semua log untuk ditampilkan di NotificationScreen
    fun getLogs(context: Context): List<NotificationLog> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LOGS, null) ?: return emptyList()

        val type = object : TypeToken<List<NotificationLog>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}