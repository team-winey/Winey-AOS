package com.android.go.sopt.winey.util.view

sealed class LocalUiState {
    object Loading : LocalUiState()
    object Success : LocalUiState()
    data class Failure(val code: Int?) : LocalUiState()
}
