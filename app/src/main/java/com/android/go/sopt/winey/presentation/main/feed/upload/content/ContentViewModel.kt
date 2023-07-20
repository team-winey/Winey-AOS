package com.android.go.sopt.winey.presentation.main.feed.upload.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class ContentViewModel : ViewModel() {
    val _content = MutableLiveData<String>()
    val content: String get() = _content.value ?: ""

    val isValidContent : LiveData<Boolean> = _content.map { validateLength(it) }

    private fun validateLength(content: String): Boolean =
        content.length in MIN_CONTENT_LENGTH..MAX_CONTENT_LENGTH

    companion object {
        const val MIN_CONTENT_LENGTH = 6
        const val MAX_CONTENT_LENGTH = 36
    }
}