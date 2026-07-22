package com.example.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream

/**
 * Tests for ImageCompressor - verifies resize and compression logic.
 * Uses Robolectric for Android graphics API access.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ImageCompressorTest {

    @Test
    fun compressImageFile_nonExistentFile_returnsFalse() {
        val file = File("/tmp/nonexistent_image.jpg")
        assertFalse(ImageCompressor.compressImageFile(file))
    }

    @Test
    fun compressImageFile_emptyFile_returnsFalse() {
        val file = File.createTempFile("empty", ".jpg")
        file.deleteOnExit()
        // File exists but is empty (0 bytes)
        assertFalse(ImageCompressor.compressImageFile(file))
    }

    @Test
    fun compressImageFile_validImage_returnsTrue() {
        val file = createTestImage(2000, 2000)
        val originalSize = file.length()

        val result = ImageCompressor.compressImageFile(file)

        assertTrue(result)
        assertTrue("Compressed file should be smaller", file.length() < originalSize)
        file.delete()
    }

    @Test
    fun compressImageFile_largeImage_resizesToMaxDimensions() {
        val file = createTestImage(4000, 3000)

        ImageCompressor.compressImageFile(file)

        // Verify the output is within bounds
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        assertTrue("Width should be <= 1920", options.outWidth <= 1920)
        assertTrue("Height should be <= 1080", options.outHeight <= 1080)
        file.delete()
    }

    @Test
    fun compressImageFile_smallImage_doesNotEnlarge() {
        // Image already smaller than max - should not upscale
        val file = createTestImage(800, 600)

        ImageCompressor.compressImageFile(file)

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        // Should remain at or below original dimensions
        assertTrue("Width should be <= 800", options.outWidth <= 800)
        assertTrue("Height should be <= 600", options.outHeight <= 600)
        file.delete()
    }

    @Test
    fun compressImageFile_maintainsAspectRatio() {
        // 4:3 aspect ratio image
        val file = createTestImage(4000, 3000)

        ImageCompressor.compressImageFile(file)

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        val ratio = options.outWidth.toFloat() / options.outHeight.toFloat()
        // Original ratio is 4/3 = 1.333...
        assertEquals(4f / 3f, ratio, 0.1f)
        file.delete()
    }

    @Test
    fun compressImageFile_producesJpeg() {
        val file = createTestImage(2000, 1500)

        ImageCompressor.compressImageFile(file)

        // Read first bytes to check JPEG magic number (0xFFD8)
        val bytes = file.readBytes()
        assertTrue("Should be JPEG", bytes.size >= 2)
        assertEquals(0xFF.toByte(), bytes[0])
        assertEquals(0xD8.toByte(), bytes[1])
        file.delete()
    }

    private fun createTestImage(width: Int, height: Int): File {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Fill with a pattern so it's not trivially compressible
        for (y in 0 until height step 10) {
            for (x in 0 until width step 10) {
                bitmap.setPixel(x, y, (x * y) or 0xFF000000.toInt())
            }
        }
        val file = File.createTempFile("test_image_", ".jpg")
        file.deleteOnExit()
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        bitmap.recycle()
        return file
    }
}
