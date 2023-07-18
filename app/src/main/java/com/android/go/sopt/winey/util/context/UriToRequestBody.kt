package com.android.go.sopt.winey.util.context

import android.content.Context
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException
import android.net.Uri
import android.provider.MediaStore.Images.Media.DISPLAY_NAME
import android.provider.MediaStore.Images.Media.SIZE
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import timber.log.Timber

class UriToRequestBody(
    context: Context,
    private val uri: Uri
) : RequestBody() {
    private val contentResolver = context.contentResolver
    private var fileName = ""
    private var size = -1L

    init {
        try {
            contentResolver.query(
                uri,
                arrayOf(SIZE, DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if(cursor.moveToFirst()) {
                    size = cursor.getLong(cursor.getColumnIndexOrThrow(SIZE))
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
                }
            }
        }catch (e: SQLiteBindOrColumnIndexOutOfRangeException) {
            Timber.e(e.message)
        }
    }

    override fun contentType(): MediaType? =
        contentResolver.getType(uri)?.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        try {
            contentResolver.openInputStream(uri).use { inputStream ->
                val source = inputStream?.source()
                if(source != null) sink.writeAll(source)
            }
        } catch (e: IllegalStateException) {
            "Couldn't open content URI for reading: $uri"
        }
    }

    override fun contentLength(): Long = size

    fun toFormData() = MultipartBody.Part.createFormData(IMAGE_FILE_KEY, fileName, this)

    companion object {
        private const val IMAGE_FILE_KEY = "feedImage"
    }
}