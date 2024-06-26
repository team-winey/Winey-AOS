package org.go.sopt.winey.util.state

sealed class InputUiState {
    object Empty : InputUiState()
    object Success : InputUiState()
    data class Failure(val code: ErrorCode) : InputUiState()
}
