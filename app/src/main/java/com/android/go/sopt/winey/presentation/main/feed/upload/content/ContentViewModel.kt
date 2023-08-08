package com.android.go.sopt.winey.presentation.main.feed.upload.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ContentViewModel : ViewModel() {
    val _content = MutableStateFlow("")
    val content: String get() = _content.value

    val isValidContent: StateFlow<Boolean> = _content.map { validateContent(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private fun validateContent(content: String): Boolean =
        content.isEmpty() || content.length in MIN_CONTENT_LENGTH..MAX_CONTENT_LENGTH

    companion object {
        private const val MIN_CONTENT_LENGTH = 6
        const val MAX_CONTENT_LENGTH = 36
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
