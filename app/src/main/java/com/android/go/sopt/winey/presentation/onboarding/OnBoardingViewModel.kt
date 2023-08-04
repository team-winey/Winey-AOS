package com.android.go.sopt.winey.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.repository.KakaoLoginRepository
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val kakaoLoginRepository: KakaoLoginRepository
): ViewModel() {
    private val _isKakaoLogin = MutableStateFlow(false)
    val isKakaoLogin = _isKakaoLogin.asStateFlow()

    val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        KakaoLoginCallback {
            _isKakaoLogin.value = true
            Timber.d("액세스토큰 ${token?.accessToken}")
            Timber.d("리프레시토큰 ${token?.refreshToken}")
        }.handleResult(token, error)
    }

    fun kakaoLogin() = viewModelScope.launch {
        kakaoLoginRepository.startKakaoLogin(kakaoLoginCallback)
    }
}
