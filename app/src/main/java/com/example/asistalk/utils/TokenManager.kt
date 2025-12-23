package com.example.asistalk.utils

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "auth_pref"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}