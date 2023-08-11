package com.android.go.sopt.winey.presentation.onboarding.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_DUPLICATE
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_INVALID_LENGTH
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_SPACE_SPECIAL_CHAR
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_UNCHECKED_DUPLICATION
import com.android.go.sopt.winey.util.view.InputUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _nickname = MutableStateFlow("")
    val nickname: String get() = _nickname.value

    private val _inputUiState: MutableStateFlow<InputUiState> =
        _nickname.map { updateInputUiState(it) }
            .mutableStateIn(
                initialValue = InputUiState.Empty,
                scope = viewModelScope
            )

    val inputUiState: StateFlow<InputUiState> = _inputUiState.asStateFlow()

    val isValidNickname: StateFlow<Boolean> = _inputUiState.map { validateNickname(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private fun validateNickname(state: InputUiState) = state == InputUiState.Success

    private val _isTextChanged = MutableStateFlow(false)
    val isTextChanged: StateFlow<Boolean> = _isTextChanged.asStateFlow()

    private val _isCheckBtnClicked = MutableStateFlow(false)
    val isCheckBtnClicked: StateFlow<Boolean> = _isCheckBtnClicked.asStateFlow()

    fun getNicknameDuplicateCheck() {
        viewModelScope.launch {
            authRepository.getNicknameDuplicateCheck(nickname)
                .onSuccess { response ->
                    if (response == null) return@onSuccess
                    Timber.e("SUCCESS GET DUPLICATION CHECK: ${response.isDuplicated}")

                    _inputUiState.value = if (response.isDuplicated) {
                        InputUiState.Failure(CODE_DUPLICATE)
                    } else {
                        InputUiState.Success
                    }
                }
                .onFailure { t ->
                    Timber.e("${t.message}")
                }
        }
    }

    fun updateTextChangedState(state: Boolean) { // updated in Activity
        _isTextChanged.value = state
    }

    fun updateDuplicateCheckButtonState(state: Boolean) { // updated in Activity
        _isCheckBtnClicked.value = state
        initDuplicateCheckButtonState()
    }

    private fun initDuplicateCheckButtonState() {
        _isCheckBtnClicked.value = false
    }

    private fun updateInputUiState(nickname: String): InputUiState {
        if (nickname.isEmpty()) return InputUiState.Empty
        if (!checkLength(nickname)) return InputUiState.Failure(CODE_INVALID_LENGTH)
        if (containsSpaceOrSpecialChar(nickname)) {
            return InputUiState.Failure(
                CODE_SPACE_SPECIAL_CHAR
            )
        }

        // 텍스트가 바뀌었는데 중복체크를 하지 않은 경우
        if (isTextChanged.value && !isCheckBtnClicked.value) {
            return InputUiState.Failure(CODE_UNCHECKED_DUPLICATION)
        }

        return InputUiState.Empty
    }

    private fun checkLength(nickname: String) = nickname.length in MIN_LENGTH..MAX_LENGTH

    private fun containsSpaceOrSpecialChar(nickname: String) =
        !Regex(REGEX_PATTERN).matches(nickname)

    // _nickname.map{} Flow -> MutableStateFlow 변환을 위한 확장 함수
    private fun <T> Flow<T>.mutableStateIn(
        initialValue: T,
        scope: CoroutineScope
    ): MutableStateFlow<T> {
        val flow = MutableStateFlow(initialValue)
        scope.launch {
            this@mutableStateIn.collect(flow)
        }
        return flow
    }

    companion object {
        private const val MIN_LENGTH = 1
        const val MAX_LENGTH = 8
        private const val REGEX_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$"
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
