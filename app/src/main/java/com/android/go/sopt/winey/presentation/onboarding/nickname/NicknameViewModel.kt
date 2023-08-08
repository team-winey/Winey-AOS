package com.android.go.sopt.winey.presentation.onboarding.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.util.code.ErrorCode.CODE_INVALID_LENGTH
import com.android.go.sopt.winey.util.code.ErrorCode.CODE_SPACE_SPECIAL_CHAR
import com.android.go.sopt.winey.util.view.InputUiState
import com.android.go.sopt.winey.util.view.InputUiState.Empty
import com.android.go.sopt.winey.util.view.InputUiState.Failure
import com.android.go.sopt.winey.util.view.InputUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NicknameViewModel : ViewModel() {
    val _nickname = MutableStateFlow("")
    val nickname: String get() = _nickname.value

    private val _inputUiState = MutableStateFlow<InputUiState>(Empty)
    val inputUiState: StateFlow<InputUiState> = _nickname.map { checkNickname(it) }
        .stateIn(
            initialValue = Empty,
            scope = viewModelScope,
            started = WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private fun checkNickname(nickname: String): InputUiState {
        if (nickname.isEmpty()) return Empty
        if (!checkLength(nickname)) return Failure(CODE_INVALID_LENGTH)
        if (containsSpaceOrSpecialChar(nickname)) return Failure(CODE_SPACE_SPECIAL_CHAR)
        return Success
    }

    private fun checkLength(nickname: String) = nickname.length in MIN_LENGTH..MAX_LENGTH

    private fun containsSpaceOrSpecialChar(nickname: String) =
        !Regex(REGEX_PATTERN).matches(nickname)

    companion object {
        private const val MIN_LENGTH = 1
        const val MAX_LENGTH = 8
        private const val REGEX_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$"
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
