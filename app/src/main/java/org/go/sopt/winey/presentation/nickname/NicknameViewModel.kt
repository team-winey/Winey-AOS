package org.go.sopt.winey.presentation.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.util.state.InputError
import org.go.sopt.winey.util.state.InputUiState
import org.go.sopt.winey.util.state.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _nickname = MutableStateFlow("")
    private val nickname: String get() = _nickname.value

    private val _inputUiState: MutableStateFlow<InputUiState> =
        _nickname.map { updateInputUiState(it) }
            .mutableStateIn(
                initialValue = InputUiState.Empty,
                scope = viewModelScope
            )

    val inputUiState: StateFlow<InputUiState> = _inputUiState.asStateFlow()

    val isValidNickname: StateFlow<Boolean> = _inputUiState.map { checkNicknameFinally(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private val _duplicateChecked = MutableStateFlow(false)
    val duplicateChecked = _duplicateChecked.asStateFlow()

    private val _patchNicknameState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val patchNicknameState = _patchNicknameState.asStateFlow()

    var prevScreenName: String? = null

    private fun updateInputUiState(nickname: String): InputUiState {
        if (containsInvalidChar(nickname)) {
            return InputUiState.Failure(InputError.Nickname.INVALID_CHAR)
        }

        return InputUiState.Empty
    }

    private fun containsInvalidChar(nickname: String) = !Regex(REGEX_PATTERN).matches(nickname)

    private fun checkNicknameFinally(state: InputUiState) = state == InputUiState.Success

    fun validateNickname(): Boolean {
        if (nickname.isBlank()) {
            _inputUiState.value = InputUiState.Failure(InputError.Nickname.BLANK_INPUT)
            return false
        }

        if (containsInvalidChar(nickname)) {
            _inputUiState.value = InputUiState.Failure(InputError.Nickname.INVALID_CHAR)
            return false
        }

        return true
    }

    fun updatePrevScreenName(intentExtraValue: String?) {
        prevScreenName = intentExtraValue
    }

    fun updateDuplicateCheckState(checked: Boolean) {
        _duplicateChecked.value = checked
    }

    fun showUncheckedDuplicationError() {
        _inputUiState.value = InputUiState.Failure(InputError.Nickname.UNCHECKED_DUPLICATION)
    }

    fun getNicknameDuplicateCheck() {
        viewModelScope.launch {
            authRepository.getNicknameDuplicateCheck(nickname)
                .onSuccess { response ->
                    if (response == null) {
                        Timber.e("닉네임 중복체크 응답값 null")
                        return@launch
                    }

                    updateDuplicateCheckState(true)
                    showDuplicateCheckResult(response.isDuplicated)
                }
                .onFailure { t ->
                    Timber.e("${t.message}")
                }
        }
    }

    private fun showDuplicateCheckResult(isDuplicated: Boolean) {
        _inputUiState.value = if (isDuplicated) {
            InputUiState.Failure(InputError.Nickname.DUPLICATED)
        } else {
            InputUiState.Success
        }
    }

    fun patchNickname() {
        viewModelScope.launch {
            _patchNicknameState.value = UiState.Loading

            authRepository.patchNickname(RequestPatchNicknameDto(nickname))
                .onSuccess { response ->
                    _patchNicknameState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    _patchNicknameState.value = UiState.Failure(t.message.toString())
                }
        }
    }

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
        const val MAX_LENGTH = 8
        private const val REGEX_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$"
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
