package com.android.go.sopt.winey.util.context

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayOutputStream
import java.io.File

class UriToRequestBody(
    context: Context,
    private val imageUri: Uri
) : RequestBody() {
    private val contentResolver = context.contentResolver
    private lateinit var selectedImageBitmap: Bitmap
    private lateinit var resizedImageBitmap: Bitmap
    private val outputStream by lazy { ByteArrayOutputStream() }

//    init {
//        try {
//            contentResolver.query(
//                imageUri,
//                arrayOf(SIZE, DISPLAY_NAME),
//                null,
//                null,
//                null
//            )?.use { cursor ->
//                if (cursor.moveToFirst()) {
//                    size = cursor.getLong(cursor.getColumnIndexOrThrow(SIZE))
//                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
//                }
//            }
//        } catch (e: SQLiteBindOrColumnIndexOutOfRangeException) {
//            Timber.e(e.message)
//        }
//    }

    override fun contentType(): MediaType = "image/jpeg".toMediaType()

    override fun writeTo(sink: BufferedSink) {
        val imageInputStream = contentResolver.openInputStream(imageUri)
        selectedImageBitmap = BitmapFactory.decodeStream(imageInputStream)
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, sink.outputStream())

        if (outputStream.toByteArray().size / ONE_MB_TO_KB > MAX_IMAGE_SIZE) {
            // todo: 리사이즈 된 비트맵을 "RequestBody 타입으로" 바꿔서 서버에 요청 보내자.
            resizedImageBitmap = compressImage(selectedImageBitmap)
        }
    }

    private fun compressImage(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val aspectRatio = originalWidth / originalHeight

        val compressedWidth = MAX_IMAGE_SIZE
        val compressedHeight = compressedWidth * aspectRatio

        return Bitmap.createScaledBitmap(bitmap, compressedWidth, compressedHeight, false)
    }

//    override fun contentType(): MediaType? =
//        contentResolver.getType(imageUri)?.toMediaTypeOrNull()
//
//    override fun writeTo(sink: BufferedSink) {
//        try {
//            contentResolver.openInputStream(imageUri).use { inputStream ->
//                val source = inputStream?.source()
//                if (source != null) sink.writeAll(source)
//            }
//        } catch (e: IllegalStateException) {
//            "Couldn't open content URI for reading: $imageUri"
//        }
//    }
//
//    override fun contentLength(): Long = size

    fun toFormData(): MultipartBody.Part {
        val imageFile = File(imageUri.path.toString())
        return MultipartBody.Part.createFormData(IMAGE_FILE_KEY, imageFile.name, this)
    }

    companion object {
        private const val IMAGE_FILE_KEY = "feedImage"

        private const val MAX_IMAGE_SIZE = 400 // 400KB
        private const val ONE_MB_TO_KB = 1024
    }
}