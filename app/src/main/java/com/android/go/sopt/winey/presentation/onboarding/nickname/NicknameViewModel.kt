package com.android.go.sopt.winey.presentation.onboarding.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_INVALID_LENGTH
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_SPACE_SPECIAL_CHAR
import com.android.go.sopt.winey.util.code.NicknameErrorCode.CODE_UNCHECKED_DUPLICATION
import com.android.go.sopt.winey.util.view.InputUiState
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _nickname = MutableStateFlow("")
    val nickname: String get() = _nickname.value

    val inputUiState: StateFlow<InputUiState> = _nickname.map { updateInputUiState(it) }
        .stateIn(
            initialValue = InputUiState.Empty,
            scope = viewModelScope,
            started = WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

//    val isValidNickname: StateFlow<Boolean> = _nickname.map { validateNickname(it) }
//        .stateIn(
//            initialValue = false,
//            scope = viewModelScope,
//            started = WhileSubscribed(PRODUCE_STOP_TIMEOUT)
//        )

    private val _isTextChanged = MutableStateFlow(false)
    val isTextChanged: StateFlow<Boolean> = _isTextChanged.asStateFlow()

    private val _isClickedCheckBtn = MutableStateFlow(false)
    val isClickedCheckBtn: StateFlow<Boolean> = _isClickedCheckBtn.asStateFlow()

//    private val _isDuplicated = MutableStateFlow(true)
//    val isDuplicated: StateFlow<Boolean> = _isDuplicated.asStateFlow()

    private val _getNicknameDuplicateCheckState =
        MutableStateFlow<UiState<ResponseGetNicknameDuplicateCheckDto>>(UiState.Empty)
    val getNicknameDuplicateCheckState: StateFlow<UiState<ResponseGetNicknameDuplicateCheckDto>> =
        _getNicknameDuplicateCheckState.asStateFlow()

    fun getNicknameDuplicateCheck() {
        viewModelScope.launch {
            authRepository.getNicknameDuplicateCheck(nickname)
                .onSuccess { response ->
                    if (response == null) return@onSuccess
                    Timber.e("SUCCESS GET DUPLICATION CHECK: ${response.isDuplicated}")
                    _getNicknameDuplicateCheckState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("${t.code()} ${t.message()}")
                        _getNicknameDuplicateCheckState.value = UiState.Failure(t.message())
                        return@onFailure
                    }

                    Timber.e("${t.message}")
                    _getNicknameDuplicateCheckState.value = UiState.Failure(t.message.toString())
                }
        }
    }

    fun updateTextChangedState(state: Boolean) { // updated in Activity
        _isTextChanged.value = state
    }

    fun updateDuplicateCheckButtonState(state: Boolean) { // updated in Activity
        _isClickedCheckBtn.value = state
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
        if (isTextChanged.value && !isClickedCheckBtn.value) {
            return InputUiState.Failure(CODE_UNCHECKED_DUPLICATION)
        }

        return InputUiState.Empty
    }

    // todo: 서버통신으로 한번 확인하고 다시 텍스트를 바꾸면, 버튼이 비활성화가 되어야 한다...
//    private fun validateNickname(nickname: String) =
//        updateInputUiState(nickname) == InputUiState.Success

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
