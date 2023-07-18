package com.android.go.sopt.winey.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import timber.log.Timber

// todo: file name, resized bitmap -> request body
class BitmapRequestBody(
    private val context: Context,
    private val uri: Uri,
    private val bitmap: Bitmap
) : RequestBody() {

    override fun contentType(): MediaType = IMAGE_CONTENT_TYPE.toMediaType()

    override fun writeTo(sink: BufferedSink) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, sink.outputStream())
    }

    private fun getFileNameFromUri(): String {
        var fileName = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)

        try {
            context.contentResolver.query(
                uri, filePathColumn,
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(filePathColumn[0])
                    fileName = cursor.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            Timber.e(e.message)
        }

        return fileName
    }

    fun toFormData() = MultipartBody.Part.createFormData(FEED_IMAGE_KEY, getFileNameFromUri(), this)

    companion object {
        private const val IMAGE_CONTENT_TYPE = "image/jpeg"
        private const val FEED_IMAGE_KEY = "feedImage"
    }
}