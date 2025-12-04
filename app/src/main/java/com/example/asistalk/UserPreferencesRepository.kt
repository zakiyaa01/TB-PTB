package com.example.asistalk

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Buat instance DataStore sebagai singleton di level top
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    // Definisikan "kunci" untuk setiap data yang ingin disimpan
    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    // Fungsi untuk menyimpan data login
    suspend fun saveLoginCredentials(username: String, password: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            if (rememberMe) {
                preferences[PreferencesKeys.USERNAME] = username
                preferences[PreferencesKeys.PASSWORD] = password
                preferences[PreferencesKeys.REMEMBER_ME] = true
            } else {
                // Jika tidak dicentang, hapus data yang tersimpan
                preferences.remove(PreferencesKeys.USERNAME)
                preferences.remove(PreferencesKeys.PASSWORD)
                preferences[PreferencesKeys.REMEMBER_ME] = false
            }
        }
    }

    // Flow untuk mendapatkan status "Remember Me" secara real-time
    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REMEMBER_ME] ?: false
    }

    // Flow untuk mendapatkan username yang tersimpan
    val savedUsernameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USERNAME] ?: ""
    }

    // Flow untuk mendapatkan password yang tersimpan
    val savedPasswordFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PASSWORD] ?: ""
    }
}
