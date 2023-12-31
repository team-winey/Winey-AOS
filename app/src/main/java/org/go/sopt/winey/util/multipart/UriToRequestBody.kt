package org.go.sopt.winey.util.multipart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import timber.log.Timber
import java.io.ByteArrayOutputStream

class UriToRequestBody(
    context: Context,
    private val imageUri: Uri
) : RequestBody() {
    private val contentResolver = context.contentResolver
    private var fileSize = -1L
    private var fileName = ""
    private var isLargeImage = false
    private var compressedImageByteArray: ByteArray? = null

    init {
        contentResolver.query(
            imageUri,
            arrayOf(MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                fileSize =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                fileName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
            }
        }

        compressImage()
    }

    private fun compressImage() {
        val inputStream = contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // 필요한 경우 회전 각도를 조정한다.
        val rotatedBitmap = rotateImageIfRequired(originalBitmap)

        val outputStream = ByteArrayOutputStream()
        val imageMbSize = calcMbSize(fileSize)
        Timber.e("BEFORE IMAGE SIZE: $imageMbSize")

        // 최대 크기를 넘는 경우에만 압축한다.
        if (imageMbSize >= MAX_MB_SIZE) {
            isLargeImage = true
            outputStream.use { byteArrayOutputStream ->
                rotatedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    calcCompressQuality(imageMbSize),
                    byteArrayOutputStream
                )
            }
            calcCompressedImageSize(outputStream.toByteArray())
        }
    }

    private fun calcCompressQuality(imageSizeMb: Double) =
        ((MAX_MB_SIZE / imageSizeMb) * 100).toInt()

    private fun calcCompressedImageSize(byteArray: ByteArray) {
        compressedImageByteArray = byteArray
        fileSize = byteArray.size.toLong()
        Timber.e("AFTER IMAGE SIZE: ${calcMbSize(fileSize)}")
    }

    private fun calcMbSize(fileSize: Long) = fileSize / (KB_PER_ONE_MB * KB_PER_ONE_MB).toDouble()

    private fun rotateImageIfRequired(originalBitmap: Bitmap): Bitmap {
        val inputStream = contentResolver.openInputStream(imageUri)

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
                else -> return originalBitmap
            }

            return Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )
        }

        return originalBitmap
    }

    override fun contentLength(): Long = fileSize

    override fun contentType(): MediaType? =
        contentResolver.getType(imageUri)?.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        if (isLargeImage) {
            compressedImageByteArray?.let(sink::write)
            return
        }

        contentResolver.openInputStream(imageUri).use { inputStream ->
            inputStream?.source()?.let(sink::writeAll)
        }
    }

    fun toFormData() = MultipartBody.Part.createFormData(KEY_NAME, fileName, this)

    companion object {
        private const val KEY_NAME = "feedImage"
        private const val KB_PER_ONE_MB = 1024
        private const val MAX_MB_SIZE = 5
    }
}
