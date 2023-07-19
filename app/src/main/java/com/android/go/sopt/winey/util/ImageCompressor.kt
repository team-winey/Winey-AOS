package com.android.go.sopt.winey.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.io.ByteArrayOutputStream

/** context, uri -> bitmap resize */
class ImageCompressor(
    private val context: Context,
    private val imageUri: Uri
) {
    /** 이미지 압축 -> 크기 축소 -> 회전 */
    fun adjustImageFormat(): Bitmap? {
        try {
            val outputStream = ByteArrayOutputStream()
            val inputStream = context.contentResolver.openInputStream(imageUri)

            // 이미지 80% 압축
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            // 압축한 파일 크기가 700KB 넘으면, 크기 축소
            val resizedBitmap = reduceImageIfRequired(outputStream, originalBitmap)

            // 회전 각도 조정
            return rotateImageIfRequired(resizedBitmap)
        } catch (e: Exception) {
            Timber.e(e.message)
        }

        return null
    }

    private fun rotateImageIfRequired(bitmap: Bitmap): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imageUri)

        inputStream?.use {
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270F)
                else -> return bitmap
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        return bitmap
    }

    private fun reduceImageIfRequired(
        outputStream: ByteArrayOutputStream,
        originalBitmap: Bitmap
    ): Bitmap {
        if (outputStream.toByteArray().size / ONE_MB_TO_KB > MAX_IMAGE_SIZE) {
            return reduceImageSize(originalBitmap)
        }
        return originalBitmap
    }

    private fun reduceImageSize(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val aspectRatio = originalWidth / originalHeight

        val compressedWidth = MAX_IMAGE_SIZE
        val compressedHeight = compressedWidth * aspectRatio
        return Bitmap.createScaledBitmap(bitmap, compressedWidth, compressedHeight, false)
    }

    companion object {
        private const val MAX_IMAGE_SIZE = 700 // KB
        private const val ONE_MB_TO_KB = 1024
    }
}