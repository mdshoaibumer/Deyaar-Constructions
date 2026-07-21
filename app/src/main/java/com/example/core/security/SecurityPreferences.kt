package com.example.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurityPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "security_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var isAppLockEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_APP_LOCK_ENABLED, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_APP_LOCK_ENABLED, value).apply()

    var backupPassword: String?
        get() = sharedPreferences.getString(KEY_BACKUP_PASSWORD, null)
        set(value) = sharedPreferences.edit().putString(KEY_BACKUP_PASSWORD, value).apply()

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_BACKUP_PASSWORD = "backup_password"
    }
}
