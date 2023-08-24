package com.android.go.sopt.winey.util.view

import com.android.go.sopt.winey.util.code.ErrorCode

sealed class InputUiState {
    object Empty : InputUiState()
    object Success : InputUiState()
    data class Failure(val code: ErrorCode) : InputUiState()
}
