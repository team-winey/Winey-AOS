package com.android.go.sopt.winey.util.view

import com.android.go.sopt.winey.util.code.NicknameErrorCode

sealed class InputUiState {
    object Empty : InputUiState()
    object Success : InputUiState()
    data class Failure(val code: NicknameErrorCode) : InputUiState()
}
