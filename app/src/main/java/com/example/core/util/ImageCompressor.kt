package com.example.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility for compressing and resizing construction site photos before storage.
 *
 * Target: Max dimension 1920x1080, JPEG quality 80%.
 * This keeps construction photos readable while significantly reducing storage and backup size.
 */
object ImageCompressor {

    private const val MAX_WIDTH = 1920
    private const val MAX_HEIGHT = 1080
    private const val JPEG_QUALITY = 80

    /**
     * Compresses and resizes an image file in-place.
     * Preserves EXIF orientation by applying rotation before saving.
     *
     * @param imageFile The source image file to compress
     * @return true if compression succeeded, false if the file could not be processed
     */
    fun compressImageFile(imageFile: File): Boolean {
        if (!imageFile.exists() || imageFile.length() == 0L) return false

        return try {
            // Read EXIF orientation before decoding
            val orientation = getExifOrientation(imageFile.absolutePath)

            // Decode with inSampleSize for memory efficiency
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imageFile.absolutePath, options)

            val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize

            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                ?: return false

            // Apply EXIF rotation
            val rotatedBitmap = applyOrientation(bitmap, orientation)

            // Scale to fit within MAX_WIDTH x MAX_HEIGHT
            val scaledBitmap = scaleBitmap(rotatedBitmap)

            // Write compressed JPEG
            FileOutputStream(imageFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.flush()
            }

            // Recycle bitmaps to free memory
            if (scaledBitmap !== rotatedBitmap) scaledBitmap.recycle()
            if (rotatedBitmap !== bitmap) rotatedBitmap.recycle()
            bitmap.recycle()

            true
        } catch (e: IOException) {
            false
        } catch (e: OutOfMemoryError) {
            false
        }
    }

    /**
     * Compresses an image from a Uri and saves it to the specified output file.
     *
     * @param context Android context for ContentResolver access
     * @param sourceUri The source image URI
     * @param outputFile The destination file for the compressed image
     * @return true if compression succeeded
     */
    fun compressFromUri(context: Context, sourceUri: Uri, outputFile: File): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return false

            // Read into temp bytes for EXIF parsing
            val bytes = inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) return false

            // Write temp file for EXIF reading
            val tempFile = File(outputFile.parent, "temp_${outputFile.name}")
            tempFile.writeBytes(bytes)

            val orientation = getExifOrientation(tempFile.absolutePath)

            // Decode with sampling
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // Clean up temp file
            tempFile.delete()

            if (bitmap == null) return false

            val rotatedBitmap = applyOrientation(bitmap, orientation)
            val scaledBitmap = scaleBitmap(rotatedBitmap)

            FileOutputStream(outputFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.flush()
            }

            if (scaledBitmap !== rotatedBitmap) scaledBitmap.recycle()
            if (rotatedBitmap !== bitmap) rotatedBitmap.recycle()
            bitmap.recycle()

            true
        } catch (e: IOException) {
            false
        } catch (e: OutOfMemoryError) {
            false
        }
    }

    /**
     * Calculates an appropriate inSampleSize for initial downsampling.
     * This prevents loading a massive bitmap into memory just to scale it down.
     */
    private fun calculateInSampleSize(width: Int, height: Int): Int {
        var inSampleSize = 1
        var w = width
        var h = height

        while (w / 2 >= MAX_WIDTH && h / 2 >= MAX_HEIGHT) {
            inSampleSize *= 2
            w /= 2
            h /= 2
        }
        return inSampleSize
    }

    /**
     * Scales a bitmap to fit within MAX_WIDTH x MAX_HEIGHT while maintaining aspect ratio.
     * If the bitmap is already within bounds, returns it unchanged.
     */
    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap
        }

        val ratioW = MAX_WIDTH.toFloat() / width.toFloat()
        val ratioH = MAX_HEIGHT.toFloat() / height.toFloat()
        val scale = minOf(ratioW, ratioH)

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Reads EXIF orientation from an image file.
     */
    private fun getExifOrientation(filePath: String): Int {
        return try {
            val exif = ExifInterface(filePath)
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: IOException) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Applies EXIF orientation rotation to a bitmap.
     */
    private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.preScale(-1f, 1f)
            }
            else -> return bitmap
        }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) {
            bitmap.recycle()
        }
        return rotated
    }
}
