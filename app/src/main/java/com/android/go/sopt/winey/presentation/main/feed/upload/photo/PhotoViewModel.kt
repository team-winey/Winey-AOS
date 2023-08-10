package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotoViewModel : ViewModel() {
    private val _photoUploadState = MutableStateFlow(false)
    val photoUploadState: StateFlow<Boolean> = _photoUploadState.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    fun activateNextButton() {
        _photoUploadState.value = true
    }

    fun showSelectedImage(imageUri: Uri) {
        _imageUri.value = imageUri
    }
}
