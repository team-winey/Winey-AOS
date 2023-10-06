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
import org.go.sopt.winey.util.code.ErrorCode
import org.go.sopt.winey.util.view.InputUiState
import org.go.sopt.winey.util.view.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _nickname = MutableStateFlow("")
    private val nickname: String get() = _nickname.value

    // Why MutableStateFlow -> map 이외의 함수에서도 값을 바꿀 수 있도록
    private val _inputUiState: MutableStateFlow<InputUiState> = _nickname.map { checkInputUiState(it) }
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

    private val _duplicateChecked = MutableStateFlow(false)
    val duplicateChecked: StateFlow<Boolean> = _duplicateChecked.asStateFlow()

    private val _patchNicknameState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val patchNicknameState: StateFlow<UiState<Unit>> = _patchNicknameState.asStateFlow()

    var prevScreenName: String? = null

    private fun checkInputUiState(nickname: String): InputUiState {
        if (nickname.isBlank()) return InputUiState.Empty
        if (!checkLength(nickname)) return InputUiState.Failure(ErrorCode.CODE_INVALID_LENGTH)
        if (containsSpaceOrSpecialChar(nickname)) {
            return InputUiState.Failure(ErrorCode.CODE_SPACE_SPECIAL_CHAR)
        }
        return InputUiState.Empty
    }

    private fun checkLength(nickname: String) = nickname.length in MIN_LENGTH..MAX_LENGTH

    private fun containsSpaceOrSpecialChar(nickname: String) =
        !Regex(REGEX_PATTERN).matches(nickname)

    // 액티비티에서 전달 -> XML 바인딩 어댑터에 사용
    fun updatePrevScreenName(intentExtraValue: String?) {
        prevScreenName = intentExtraValue
    }

    // 중복체크 하지 않고 시작하기 버튼 눌렀을 때 -> Failure 상태로 갱신
    fun updateInputUiState(inputUiState: InputUiState) {
        _inputUiState.value = inputUiState
    }

    // 액티비티, 뷰모델에서 갱신
    fun updateDuplicateCheckState(checked: Boolean) {
        _duplicateChecked.value = checked
    }

    fun checkValidInput(): Boolean {
        if (nickname.isBlank()) {
            _inputUiState.value = InputUiState.Failure(ErrorCode.CODE_BLANK_INPUT)
            return false
        }

        if (containsSpaceOrSpecialChar(nickname)) {
            _inputUiState.value = InputUiState.Failure(ErrorCode.CODE_SPACE_SPECIAL_CHAR)
            return false
        }

        return true
    }

    fun getNicknameDuplicateCheck() {
        viewModelScope.launch {
            authRepository.getNicknameDuplicateCheck(nickname)
                .onSuccess { response ->
                    if (response == null) return@onSuccess
                    showDuplicateCheckResult(response.isDuplicated)

                    updateDuplicateCheckState(true)
                    Timber.d("DUPLICATE CHECK: ${duplicateChecked.value}")
                }
                .onFailure { t ->
                    Timber.e("${t.message}")
                }
        }
    }

    private fun showDuplicateCheckResult(isDuplicated: Boolean) {
        _inputUiState.value = if (isDuplicated) {
            InputUiState.Failure(ErrorCode.CODE_DUPLICATED)
        } else {
            InputUiState.Success
        }
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
