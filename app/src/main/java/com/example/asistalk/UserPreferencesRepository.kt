package com.example.asistalk.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// SINGLE DataStore instance
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    // ===== KEYS =====
    private object Keys {
        val USER_ID = intPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val FULLNAME = stringPreferencesKey("fullname")
        val EMAIL = stringPreferencesKey("email")
        val PHONE = stringPreferencesKey("phone")
        val BIRTH_DATE = stringPreferencesKey("birth_date")
        val GENDER = stringPreferencesKey("gender")
        val PROFILE_IMAGE = stringPreferencesKey("profile_image")
        val PASSWORD = stringPreferencesKey("password")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        // Token tidak lagi disimpan di DataStore agar sinkron dengan AuthInterceptor
    }

    // ===== LOGIN CREDENTIALS =====
    suspend fun saveLoginCredentials(
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        context.dataStore.edit { prefs ->
            if (rememberMe) {
                prefs[Keys.USERNAME] = username
                prefs[Keys.PASSWORD] = password
                prefs[Keys.REMEMBER_ME] = true
            } else {
                prefs.remove(Keys.USERNAME)
                prefs.remove(Keys.PASSWORD)
                prefs[Keys.REMEMBER_ME] = false
            }
        }
    }

    // ===== TOKEN (DIALIKKAN KE TOKENMANAGER) =====
    // Perbaikan: Simpan ke SharedPreferences agar terbaca oleh Interceptor
    fun saveToken(token: String) {
        TokenManager.saveToken(context, token)
    }

    fun getToken(): String {
        return TokenManager.getToken(context) ?: ""
    }

    // ===== USER PROFILE (LENGKAP) =====
    suspend fun saveFullProfile(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        birthDate: String,
        gender: String,
        profileImage: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USERNAME] = username
            prefs[Keys.FULLNAME] = fullName
            prefs[Keys.EMAIL] = email
            prefs[Keys.PHONE] = phone
            prefs[Keys.BIRTH_DATE] = birthDate
            prefs[Keys.GENDER] = gender
            prefs[Keys.PROFILE_IMAGE] = profileImage
        }
    }

    // Tambahan fungsi untuk simpan ID saja jika diperlukan terpisah
    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
        }
    }

    // ===== FLOWS (DIBACA UI & VIEWMODEL) =====
    val userIdFlow: Flow<Int> = context.dataStore.data.map {
        it[Keys.USER_ID] ?: -1
    }

    val fullnameFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.FULLNAME] ?: ""
    }

    val usernameFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.USERNAME] ?: ""
    }

    val emailFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.EMAIL] ?: ""
    }

    val phoneFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.PHONE] ?: ""
    }

    val birthDateFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.BIRTH_DATE] ?: ""
    }

    val genderFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.GENDER] ?: ""
    }

    val profileImageFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.PROFILE_IMAGE] ?: ""
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.REMEMBER_ME] ?: false
    }

    val savedUsernameFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.USERNAME] ?: ""
    }

    val savedPasswordFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.PASSWORD] ?: ""
    }

    // ===== LOGOUT =====
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
        TokenManager.clearToken(context) // Hapus token juga
    }
}