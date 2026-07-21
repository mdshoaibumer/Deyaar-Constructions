package com.example.core.util

import android.content.Context
import java.io.File
import java.util.UUID

class FileManager(private val context: Context) {

    fun getImagesDir(): File {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getPdfsDir(): File {
        val dir = File(context.filesDir, "pdfs")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getExportsDir(): File {
        val dir = File(context.filesDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun generateUniqueFilePath(directory: File, extension: String): String {
        return File(directory, "\${UUID.randomUUID()}.$extension").absolutePath
    }
}
