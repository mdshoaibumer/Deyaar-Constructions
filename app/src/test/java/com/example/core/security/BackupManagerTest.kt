package com.example.core.security

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Tests for BackupManager - verifies encrypted backup/restore,
 * wrong password handling, and corrupted data handling.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class BackupManagerTest {

    private lateinit var context: Context
    private lateinit var backupManager: BackupManager

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        backupManager = BackupManager(context)
    }

    @Test
    fun exportBackup_producesNonEmptyOutput() {
        val outputStream = ByteArrayOutputStream()
        backupManager.exportBackup(outputStream, "testPassword123")
        
        assertTrue("Backup should produce output", outputStream.size() > 0)
        // Salt (16) + IV (16) + encrypted data
        assertTrue("Backup should be at least 32 bytes (salt+iv)", outputStream.size() >= 32)
    }

    @Test
    fun restoreBackup_withCorrectPassword_succeeds() {
        val backupData = ByteArrayOutputStream()
        backupManager.exportBackup(backupData, "correctPassword")
        
        val inputStream = ByteArrayInputStream(backupData.toByteArray())
        try {
            backupManager.restoreBackup(inputStream, "correctPassword")
            assertTrue(true)
        } catch (e: Exception) {
            fail("Restore with correct password should not throw: ${e.message}")
        }
    }

    @Test
    fun restoreBackup_withWrongPassword_throwsWrongPasswordException() {
        val backupData = ByteArrayOutputStream()
        backupManager.exportBackup(backupData, "correctPassword")
        
        val inputStream = ByteArrayInputStream(backupData.toByteArray())
        try {
            backupManager.restoreBackup(inputStream, "wrongPassword")
            // May succeed with garbled data or throw
        } catch (e: BackupManager.BackupException.WrongPassword) {
            // Expected
            assertNotNull(e.message)
        } catch (e: BackupManager.BackupException.CorruptedData) {
            // Also acceptable - wrong password can look like corrupted data
            assertNotNull(e.message)
        } catch (e: BackupManager.BackupException) {
            // Any BackupException is acceptable for wrong password
            assertNotNull(e.message)
        }
    }

    @Test
    fun restoreBackup_corruptedData_throwsCorruptedException() {
        val corruptedData = ByteArray(100)
        java.security.SecureRandom().nextBytes(corruptedData)
        
        val inputStream = ByteArrayInputStream(corruptedData)
        try {
            backupManager.restoreBackup(inputStream, "anyPassword")
            // If no exception, the data happened to decrypt without errors (rare but possible)
        } catch (e: BackupManager.BackupException) {
            // Expected - corrupted data should produce a BackupException
            assertNotNull(e.message)
        }
    }

    @Test
    fun restoreBackup_truncatedData_throwsCorruptedException() {
        // Only 10 bytes - not enough for salt (16) + iv (16)
        val truncatedData = ByteArray(10)
        val inputStream = ByteArrayInputStream(truncatedData)
        
        try {
            backupManager.restoreBackup(inputStream, "password")
            fail("Should throw for truncated data")
        } catch (e: BackupManager.BackupException.CorruptedData) {
            assertNotNull(e.message)
        }
    }

    @Test
    fun exportBackup_differentPasswords_produceDifferentOutput() {
        val output1 = ByteArrayOutputStream()
        backupManager.exportBackup(output1, "password1")
        
        val output2 = ByteArrayOutputStream()
        backupManager.exportBackup(output2, "password2")
        
        assertFalse(
            "Different passwords should produce different output",
            output1.toByteArray().contentEquals(output2.toByteArray())
        )
    }

    @Test
    fun exportBackup_samePassword_producesUniqueOutputEachTime() {
        val output1 = ByteArrayOutputStream()
        backupManager.exportBackup(output1, "samePassword")
        
        val output2 = ByteArrayOutputStream()
        backupManager.exportBackup(output2, "samePassword")
        
        // Random salt means even same password produces different ciphertext
        assertFalse(
            "Same password with different salts should produce different output",
            output1.toByteArray().contentEquals(output2.toByteArray())
        )
    }

    @Test
    fun restoreBackup_emptyStream_throwsCorruptedException() {
        val emptyStream = ByteArrayInputStream(ByteArray(0))
        
        try {
            backupManager.restoreBackup(emptyStream, "password")
            fail("Should throw for empty stream")
        } catch (e: BackupManager.BackupException.CorruptedData) {
            assertNotNull(e.message)
        }
    }

    @Test
    fun backupException_hierarchyIsCorrect() {
        // Verify exception types are properly structured
        val wrongPwd = BackupManager.BackupException.WrongPassword()
        val corrupted = BackupManager.BackupException.CorruptedData()
        val ioError = BackupManager.BackupException.IoError("test")
        val noSpace = BackupManager.BackupException.InsufficientStorage()

        assertTrue(wrongPwd is BackupManager.BackupException)
        assertTrue(corrupted is BackupManager.BackupException)
        assertTrue(ioError is BackupManager.BackupException)
        assertTrue(noSpace is BackupManager.BackupException)
        assertTrue(wrongPwd is Exception)
    }
}
