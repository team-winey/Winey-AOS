package com.android.go.sopt.winey.presentation.onboarding.story

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StoryViewModel : ViewModel() {
    private val _pageNumber = MutableStateFlow(1)
    val pageNumber: StateFlow<Int> = _pageNumber.asStateFlow()

    private val _detailText = MutableStateFlow("")
    val detailText: StateFlow<String> = _detailText.asStateFlow()

    fun updatePageNumber(number: Int) {
        _pageNumber.value = number
    }

    fun updateDetailText(text: String) {
        _detailText.value = text
    }
}
