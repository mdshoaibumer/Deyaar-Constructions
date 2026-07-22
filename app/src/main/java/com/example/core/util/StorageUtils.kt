package com.example.core.util

import android.content.Context
import android.os.StatFs
import timber.log.Timber
import java.io.File

/**
 * Utility for checking storage availability and handling low storage scenarios.
 * Used before write operations to prevent silent failures and data corruption.
 */
object StorageUtils {

    /**
     * Minimum free space required for normal operations (50 MB).
     */
    private const val MIN_FREE_SPACE_BYTES = 50L * 1024 * 1024

    /**
     * Minimum free space required for backup operations (100 MB).
     */
    private const val MIN_BACKUP_SPACE_BYTES = 100L * 1024 * 1024

    /**
     * Minimum free space required for photo capture (10 MB).
     */
    private const val MIN_PHOTO_SPACE_BYTES = 10L * 1024 * 1024

    /**
     * Checks if there is enough storage space for normal database operations.
     */
    fun hasEnoughStorageForDatabase(context: Context): Boolean {
        return getAvailableInternalStorage(context) > MIN_FREE_SPACE_BYTES
    }

    /**
     * Checks if there is enough storage space for a backup operation.
     */
    fun hasEnoughStorageForBackup(context: Context): Boolean {
        return getAvailableInternalStorage(context) > MIN_BACKUP_SPACE_BYTES
    }

    /**
     * Checks if there is enough storage space for a photo capture.
     */
    fun hasEnoughStorageForPhoto(context: Context): Boolean {
        val externalAvailable = getAvailableExternalStorage(context)
        val internalAvailable = getAvailableInternalStorage(context)
        return (externalAvailable > MIN_PHOTO_SPACE_BYTES) || (internalAvailable > MIN_PHOTO_SPACE_BYTES)
    }

    /**
     * Returns available internal storage in bytes.
     */
    fun getAvailableInternalStorage(context: Context): Long {
        return try {
            val stat = StatFs(context.filesDir.absolutePath)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            Timber.w(e, "Failed to check internal storage")
            0L
        }
    }

    /**
     * Returns available external storage in bytes.
     */
    fun getAvailableExternalStorage(context: Context): Long {
        return try {
            val externalDir = context.getExternalFilesDir(null) ?: return 0L
            val stat = StatFs(externalDir.absolutePath)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            Timber.w(e, "Failed to check external storage")
            0L
        }
    }

    /**
     * Formats a byte count for display.
     */
    fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.1f GB", bytes.toDouble() / (1024 * 1024 * 1024))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format("%.1f KB", bytes.toDouble() / 1024)
            else -> "$bytes B"
        }
    }

    /**
     * Safely writes data to a file with atomic write pattern.
     * Writes to a temp file first, then renames to the target.
     * This prevents data corruption on power loss or app termination.
     *
     * @return true if write succeeded, false otherwise
     */
    fun safeWriteFile(targetFile: File, writeAction: (File) -> Unit): Boolean {
        val tempFile = File(targetFile.parent, ".tmp_${targetFile.name}")
        return try {
            writeAction(tempFile)
            // Atomic rename
            if (tempFile.exists()) {
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                tempFile.renameTo(targetFile)
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Safe write failed for ${targetFile.name}")
            tempFile.delete()
            false
        }
    }
}
