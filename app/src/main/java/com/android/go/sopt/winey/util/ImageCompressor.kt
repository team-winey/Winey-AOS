package com.android.go.sopt.winey.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

// todo: context, uri -> bitmap -> resize
class ImageCompressor(
    private val context: Context,
    private val imageUri: Uri
) {
    fun getResizedImageBitmap(): Bitmap {
        val outputStream = ByteArrayOutputStream()
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        if(outputStream.toByteArray().size / ONE_MB_TO_KB > MAX_IMAGE_SIZE) {
            return compressImage(originalBitmap)
        }

        return originalBitmap
    }

    private fun compressImage(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val aspectRatio = originalWidth / originalHeight

        val compressedWidth = MAX_IMAGE_SIZE
        val compressedHeight = compressedWidth * aspectRatio

        return Bitmap.createScaledBitmap(bitmap, compressedWidth, compressedHeight, false)
    }

    companion object {
        private const val MAX_IMAGE_SIZE = 400 // 400KB
        private const val ONE_MB_TO_KB = 1024
    }
}