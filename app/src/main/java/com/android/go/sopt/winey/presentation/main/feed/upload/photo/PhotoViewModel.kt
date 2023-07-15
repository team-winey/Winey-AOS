package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    private val _uploadState = MutableLiveData<Boolean>()
    val uploadState: LiveData<Boolean> get() = _uploadState

    fun activateNextButton() {
        _uploadState.value = true
    }
}