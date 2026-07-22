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

    fun isPinEnabled(): Boolean {
        return sharedPreferences.getString(KEY_PIN, null) != null
    }

    fun setPin(pin: String) {
        sharedPreferences.edit()
            .putString(KEY_PIN, pin)
            .putBoolean(KEY_APP_LOCK_ENABLED, true)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = sharedPreferences.getString(KEY_PIN, null)
        return stored != null && stored == pin
    }

    fun clearPin() {
        sharedPreferences.edit()
            .remove(KEY_PIN)
            .putBoolean(KEY_APP_LOCK_ENABLED, false)
            .apply()
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_BACKUP_PASSWORD = "backup_password"
        private const val KEY_PIN = "user_pin"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
}
