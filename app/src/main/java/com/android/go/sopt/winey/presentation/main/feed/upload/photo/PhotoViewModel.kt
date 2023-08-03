package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private val _photoSelected = MutableLiveData<Boolean>()
    val photoSelected: LiveData<Boolean> get() = _photoSelected

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    fun updatePhotoSelectState() {
        _photoSelected.value = true
    }

    fun updateImageUri(imageUri: Uri) {
        _imageUri.value = imageUri
    }
}
