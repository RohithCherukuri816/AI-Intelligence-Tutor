package com.example.eduaituitor.repository

import com.example.eduaituitor.data.local.DataStoreManager
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStoreManager: DataStoreManager) {
    val theme: Flow<String> = dataStoreManager.themeFlow
    val ttsEnabled: Flow<Boolean> = dataStoreManager.ttsEnabledFlow

    suspend fun updateTheme(theme: String) {
        dataStoreManager.saveTheme(theme)
    }

    suspend fun updateTtsEnabled(enabled: Boolean) {
        dataStoreManager.saveTtsEnabled(enabled)
    }
}
