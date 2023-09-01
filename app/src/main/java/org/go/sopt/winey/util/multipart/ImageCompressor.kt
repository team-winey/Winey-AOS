package org.go.sopt.winey.util.multipart

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
    fun adjustImageFormat(): Bitmap? {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val outputStream = ByteArrayOutputStream()

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            val resizedBitmap = reduceImageSizeIfRequired(outputStream, originalBitmap)

            return rotateImageIfRequired(resizedBitmap)
        } catch (e: Exception) {
            Timber.e(e.message)
        }
        return null
    }

    private fun reduceImageSizeIfRequired(
        outputStream: ByteArrayOutputStream,
        originalBitmap: Bitmap
    ): Bitmap {
        // 서버에서 수용 가능한 최대 이미지 사이즈 보다 크면 80 퍼센트 압축하기
        if (isOverMaxSize(outputStream)) {
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, outputStream)
        }

        return originalBitmap
    }

    private fun isOverMaxSize(outputStream: ByteArrayOutputStream) =
        outputStream.toByteArray().size / KB_PER_ONE_MB > MAX_IMAGE_SIZE

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

    companion object {
        private const val COMPRESS_QUALITY = 80
        private const val MAX_IMAGE_SIZE = 5000 // 5MB
        private const val KB_PER_ONE_MB = 1024
    }
}
