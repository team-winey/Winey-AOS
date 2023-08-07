package com.android.go.sopt.winey.presentation.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NicknameViewModel : ViewModel() {
    private val _nickname = MutableLiveData<String>()
    val nickname: String get() = _nickname.value ?: ""

//    val isValidNickname: LiveData<Boolean> = _nickname.map { validateNickname(it) }
//
//    private fun validateNickname(nickname: String) =
//        checkLength(nickname) && !containsSpaceOrSpecialChar(nickname)

    private fun checkLength(nickname: String) = nickname.length in MIN_LENGTH..MAX_LENGTH

    private fun containsSpaceOrSpecialChar(nickname: String) =
        !Regex(REGEX_PATTERN).matches(nickname)

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 8
        private const val REGEX_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$"
    }
}
