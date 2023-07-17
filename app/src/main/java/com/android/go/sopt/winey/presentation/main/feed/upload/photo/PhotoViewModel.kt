package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> get() = _isUploaded

    fun updateImageUri(imageUri: Uri){
        _imageUri.value = imageUri
    }

    fun activateNextButton() {
        _isUploaded.value = true
    }
}