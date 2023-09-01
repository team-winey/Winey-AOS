package org.go.sopt.winey.util.multipart

import android.content.Context
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import timber.log.Timber

class UriToRequestBody(
    context: Context,
    private val imageUri: Uri
) : RequestBody() {
    private val contentResolver = context.contentResolver
    private var fileName = ""
    private var size = -1L

    init {
        try {
            contentResolver.query(
                imageUri,
                arrayOf(MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                }
            }
        } catch (e: SQLiteBindOrColumnIndexOutOfRangeException) {
            Timber.e(e.message)
        }
    }

    override fun contentType(): MediaType? =
        contentResolver.getType(imageUri)?.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        try {
            contentResolver.openInputStream(imageUri).use { inputStream ->
                val source = inputStream?.source()
                if (source != null) sink.writeAll(source)
            }
        } catch (e: IllegalStateException) {
            "Couldn't open content URI for reading: $imageUri"
        }
    }

    override fun contentLength(): Long = size

    fun toFormData() = MultipartBody.Part.createFormData(KEY_NAME, fileName, this)

    companion object {
        private const val KEY_NAME = "feedImage"
    }
}
