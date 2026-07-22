package com.example.core.security

import android.content.Context
import timber.log.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

/**
 * Manages encrypted backup and restore of the application database and files.
 * Uses AES-256/CBC with PBKDF2 key derivation for security.
 *
 * Error handling:
 * - Wrong password: throws BackupException with WRONG_PASSWORD reason
 * - Corrupted ZIP: throws BackupException with CORRUPTED_DATA reason
 * - Disk full / IO errors: throws BackupException with IO_ERROR reason
 * - Cancelled/interrupted: throws BackupException with INTERRUPTED reason
 */
class BackupManager(private val context: Context) {

    sealed class BackupException(message: String, cause: Throwable? = null) : Exception(message, cause) {
        class WrongPassword(cause: Throwable? = null) : BackupException("Invalid password - decryption failed", cause)
        class CorruptedData(cause: Throwable? = null) : BackupException("Backup data is corrupted", cause)
        class IoError(message: String, cause: Throwable? = null) : BackupException(message, cause)
        class InsufficientStorage(cause: Throwable? = null) : BackupException("Insufficient storage space", cause)
    }

    private fun getSecretKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    /**
     * Creates an encrypted backup and writes it to the output stream.
     *
     * @param outputStream Destination for the backup data
     * @param password Encryption password
     * @throws BackupException.IoError if writing fails
     * @throws BackupException.InsufficientStorage if disk is full
     */
    fun exportBackup(outputStream: OutputStream, password: String) {
        try {
            val salt = ByteArray(16)
            SecureRandom().nextBytes(salt)

            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)

            outputStream.write(salt)
            outputStream.write(iv)

            val secretKey = getSecretKey(password, salt)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

            val cipherOut = CipherOutputStream(outputStream, cipher)
            val zipOut = ZipOutputStream(cipherOut)

            // Write database file
            val dbFile = context.getDatabasePath("deyaar_construction.db")
            if (dbFile.exists()) {
                addFileToZip(zipOut, dbFile, "database/deyaar_construction.db")
            }

            // Write WAL file if exists (for consistency)
            val walFile = File(dbFile.absolutePath + "-wal")
            if (walFile.exists()) {
                addFileToZip(zipOut, walFile, "database/deyaar_construction.db-wal")
            }

            // Write SHM file if exists
            val shmFile = File(dbFile.absolutePath + "-shm")
            if (shmFile.exists()) {
                addFileToZip(zipOut, shmFile, "database/deyaar_construction.db-shm")
            }

            // Write backup metadata
            val entry = ZipEntry("backup_info.txt")
            zipOut.putNextEntry(entry)
            val metadata = "version=9\ntimestamp=${System.currentTimeMillis()}\napp=deyaar_construction"
            zipOut.write(metadata.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            zipOut.close()
            Timber.i("Backup export completed successfully")
        } catch (e: IOException) {
            Timber.e(e, "Backup export failed - IO error")
            if (e.message?.contains("No space") == true || e.message?.contains("ENOSPC") == true) {
                throw BackupException.InsufficientStorage(e)
            }
            throw BackupException.IoError("Failed to create backup: ${e.message}", e)
        } catch (e: Exception) {
            Timber.e(e, "Backup export failed")
            throw BackupException.IoError("Failed to create backup: ${e.message}", e)
        }
    }

    /**
     * Restores an encrypted backup from the input stream.
     *
     * @param inputStream Source of the backup data
     * @param password Decryption password
     * @throws BackupException.WrongPassword if the password is incorrect
     * @throws BackupException.CorruptedData if the backup data is malformed
     * @throws BackupException.IoError if reading fails
     */
    fun restoreBackup(inputStream: InputStream, password: String) {
        try {
            val salt = ByteArray(16)
            val saltRead = inputStream.read(salt)
            if (saltRead != 16) {
                throw BackupException.CorruptedData()
            }

            val iv = ByteArray(16)
            val ivRead = inputStream.read(iv)
            if (ivRead != 16) {
                throw BackupException.CorruptedData()
            }

            val secretKey = getSecretKey(password, salt)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

            val cipherIn = CipherInputStream(inputStream, cipher)
            val zipIn = ZipInputStream(cipherIn)

            var entryCount = 0
            var entry: ZipEntry? = zipIn.nextEntry
            while (entry != null) {
                entryCount++
                if (entryCount > 1000) {
                    // Protect against zip bombs
                    throw BackupException.CorruptedData()
                }

                when {
                    entry.name.startsWith("database/") -> {
                        restoreDatabaseFile(zipIn, entry.name.removePrefix("database/"))
                    }
                    entry.name == "backup_info.txt" -> {
                        // Read and validate metadata
                        val metadataBytes = zipIn.readBytes()
                        val metadata = String(metadataBytes, Charsets.UTF_8)
                        Timber.d("Restoring backup: $metadata")
                    }
                    // Skip unknown entries gracefully
                }
                entry = zipIn.nextEntry
            }
            zipIn.close()

            if (entryCount == 0) {
                throw BackupException.CorruptedData()
            }

            Timber.i("Backup restore completed successfully ($entryCount entries)")
        } catch (e: BackupException) {
            throw e // Re-throw our own exceptions
        } catch (e: javax.crypto.BadPaddingException) {
            Timber.w("Backup restore failed - wrong password")
            throw BackupException.WrongPassword(e)
        } catch (e: java.util.zip.ZipException) {
            Timber.w(e, "Backup restore failed - corrupted zip")
            throw BackupException.CorruptedData(e)
        } catch (e: IOException) {
            Timber.e(e, "Backup restore failed - IO error")
            throw BackupException.IoError("Failed to restore backup: ${e.message}", e)
        } catch (e: Exception) {
            Timber.e(e, "Backup restore failed - unexpected error")
            throw BackupException.CorruptedData(e)
        }
    }

    private fun addFileToZip(zipOut: ZipOutputStream, file: File, entryName: String) {
        val entry = ZipEntry(entryName)
        zipOut.putNextEntry(entry)
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var len: Int
            while (fis.read(buffer).also { len = it } > 0) {
                zipOut.write(buffer, 0, len)
            }
        }
        zipOut.closeEntry()
    }

    private fun restoreDatabaseFile(zipIn: ZipInputStream, fileName: String) {
        val dbDir = context.getDatabasePath("deyaar_construction.db").parentFile
            ?: throw BackupException.IoError("Cannot access database directory")

        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }

        val targetFile = File(dbDir, fileName)
        try {
            FileOutputStream(targetFile).use { fos ->
                val buffer = ByteArray(8192)
                var len: Int
                while (zipIn.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.flush()
            }
        } catch (e: IOException) {
            if (e.message?.contains("No space") == true || e.message?.contains("ENOSPC") == true) {
                throw BackupException.InsufficientStorage(e)
            }
            throw e
        }
    }
}
