package com.example.eduaituitor.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val TTS_ENABLED_KEY = booleanPreferencesKey("tts_enabled")
    }

    val themeFlow: Flow<String> = dataStore.data.map { it[THEME_KEY] ?: "system" }
    val ttsEnabledFlow: Flow<Boolean> = dataStore.data.map { it[TTS_ENABLED_KEY] ?: false }

    suspend fun saveTheme(theme: String) {
        dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun saveTtsEnabled(enabled: Boolean) {
        dataStore.edit { it[TTS_ENABLED_KEY] = enabled }
    }
}
