package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "deyaar_settings")

class AppPreferencesManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        val KEY_DARK_MODE = stringPreferencesKey("dark_mode") // SYSTEM, LIGHT, DARK
        val KEY_CURRENCY = stringPreferencesKey("currency")
        val KEY_COMPANY_NAME = stringPreferencesKey("company_name")
        val KEY_BACKUP_LOCATION = stringPreferencesKey("backup_location")
    }

    val isPinEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_PIN_ENABLED] ?: false
    }

    val darkModeConfig: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: "SYSTEM"
    }

    val currency: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_CURRENCY] ?: "USD"
    }

    val companyName: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_COMPANY_NAME] ?: "DEYAAR CONSTRUCTIONS"
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_PIN_ENABLED] = enabled
        }
    }

    suspend fun setDarkMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = mode
        }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[KEY_CURRENCY] = currency
        }
    }

    suspend fun setCompanyName(name: String) {
        dataStore.edit { preferences ->
            preferences[KEY_COMPANY_NAME] = name
        }
    }
}
