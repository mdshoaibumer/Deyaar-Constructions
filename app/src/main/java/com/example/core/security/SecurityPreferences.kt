package com.example.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Manages security preferences with proper PIN hashing.
 * PIN is stored as a PBKDF2-HMAC-SHA256 hash with a unique random salt.
 * Defense-in-depth: even though the SharedPreferences are AES-encrypted,
 * the PIN itself is never stored in recoverable form.
 */
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
        return sharedPreferences.getString(KEY_PIN_HASH, null) != null
    }

    /**
     * Stores the PIN as a salted PBKDF2 hash.
     * Format stored: "base64(salt):base64(hash)"
     */
    fun setPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

        sharedPreferences.edit()
            .putString(KEY_PIN_HASH, "$saltBase64:$hashBase64")
            .remove(KEY_PIN_LEGACY) // Remove any legacy plaintext PIN
            .putBoolean(KEY_APP_LOCK_ENABLED, true)
            .apply()
    }

    /**
     * Verifies a PIN against the stored hash.
     * Also handles migration from legacy plaintext storage.
     */
    fun verifyPin(pin: String): Boolean {
        // Try new hashed format first
        val storedHash = sharedPreferences.getString(KEY_PIN_HASH, null)
        if (storedHash != null && storedHash.contains(":")) {
            val parts = storedHash.split(":")
            if (parts.size == 2) {
                val salt = Base64.decode(parts[0], Base64.NO_WRAP)
                val expectedHash = Base64.decode(parts[1], Base64.NO_WRAP)
                val actualHash = hashPin(pin, salt)
                return constantTimeEquals(expectedHash, actualHash)
            }
        }

        // Legacy migration: if old plaintext PIN exists, verify and upgrade
        val legacyPin = sharedPreferences.getString(KEY_PIN_LEGACY, null)
        if (legacyPin != null && legacyPin == pin) {
            // Migrate to hashed storage
            setPin(pin)
            return true
        }

        return false
    }

    fun clearPin() {
        sharedPreferences.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_LEGACY)
            .putBoolean(KEY_APP_LOCK_ENABLED, false)
            .apply()
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    // --- Private helpers ---

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun hashPin(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    /**
     * Constant-time comparison to prevent timing attacks.
     */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_BACKUP_PASSWORD = "backup_password"
        private const val KEY_PIN_HASH = "user_pin_hash"
        private const val KEY_PIN_LEGACY = "user_pin" // Legacy key for migration
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val ITERATIONS = 120_000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 16
    }
}
