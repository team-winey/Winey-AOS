package com.android.go.sopt.winey.presentation.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.code.ErrorCode.CODE_DUPLICATE
import com.android.go.sopt.winey.util.code.ErrorCode.CODE_INVALID_LENGTH
import com.android.go.sopt.winey.util.code.ErrorCode.CODE_SPACE_SPECIAL_CHAR
import com.android.go.sopt.winey.util.view.InputUiState
import com.android.go.sopt.winey.util.view.UiState
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
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _nickname = MutableStateFlow("")
    val nickname: String get() = _nickname.value

    private val _inputUiState: MutableStateFlow<InputUiState> =
        _nickname.map { checkInputUiState(it) }
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

    private val _isDuplicateChecked = MutableStateFlow(false)
    val isDuplicateChecked: StateFlow<Boolean> = _isDuplicateChecked.asStateFlow()

    private val _patchNicknameState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val patchNicknameState: StateFlow<UiState<Unit>> = _patchNicknameState.asStateFlow()

    private var prevCheckResult: Pair<String, Boolean>? = null

    var prevScreenName: String? = null

    fun updatePrevScreenName(name: String?) {
        prevScreenName = name
    }

    fun updateInputUiState(inputUiState: InputUiState) {
        _inputUiState.value = inputUiState
    }

    fun updateDuplicateCheckState(checked: Boolean) {
        _isDuplicateChecked.value = checked
    }

    fun patchNickname() {
        viewModelScope.launch {
            _patchNicknameState.value = UiState.Loading

            authRepository.patchNickname(RequestPatchNicknameDto(nickname))
                .onSuccess { response ->
                    Timber.d("SUCCESS PATCH NICKNAME")
                    _patchNicknameState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP FAIL PATCH NICKNAME: ${t.code()} ${t.message}")
                        return@onFailure
                    }
                    Timber.e("FAIL PATCH NICKNAME: ${t.message}")
                    _patchNicknameState.value = UiState.Failure(t.message.toString())
                }
        }
    }

    fun getNicknameDuplicateCheck() {
        viewModelScope.launch {
            authRepository.getNicknameDuplicateCheck(nickname)
                .onSuccess { response ->
                    if (response == null) return@onSuccess

                    response.isDuplicated.let {
                        showDuplicateCheckResult(it)
                        saveDuplicateCheckResult(it)
                    }
                    updateDuplicateCheckState(true)
                }
                .onFailure { t ->
                    Timber.e("${t.message}")
                }
        }
    }

    private fun showDuplicateCheckResult(isDuplicated: Boolean) {
        _inputUiState.value = if (isDuplicated) {
            InputUiState.Failure(CODE_DUPLICATE)
        } else {
            InputUiState.Success
        }
    }

    private fun saveDuplicateCheckResult(isDuplicated: Boolean) {
        prevCheckResult = Pair(nickname, isDuplicated)
    }

    private fun checkInputUiState(nickname: String): InputUiState {
        if (nickname.isEmpty()) return InputUiState.Empty
        if (!checkLength(nickname)) return InputUiState.Failure(CODE_INVALID_LENGTH)
        if (containsSpaceOrSpecialChar(nickname)) {
            return InputUiState.Failure(CODE_SPACE_SPECIAL_CHAR)
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
