package org.go.sopt.winey.util.view

import org.go.sopt.winey.util.code.ErrorCode

sealed class InputUiState {
    object Empty : InputUiState()
    object Success : InputUiState()
    data class Failure(val code: ErrorCode) : InputUiState()
}
