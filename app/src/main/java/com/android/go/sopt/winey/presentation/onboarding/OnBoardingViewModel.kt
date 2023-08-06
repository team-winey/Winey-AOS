package com.android.go.sopt.winey.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.domain.entity.Login
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.domain.repository.KakaoLoginRepository
import com.android.go.sopt.winey.util.view.UiState
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val kakaoLoginRepository: KakaoLoginRepository,
    private val authRepository: AuthRepository,
    private val _isKakaoLogin = MutableStateFlow(false)
    val isKakaoLogin = _isKakaoLogin.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<Login>>(UiState.Loading)
    val loginState: StateFlow<UiState<Login>> get() = _loginState.asStateFlow()

    val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        KakaoLoginCallback {
            _isKakaoLogin.value = true
            Timber.d("액세스토큰 ${token?.accessToken}")
            Timber.d("리프레시토큰 ${token?.refreshToken}")
            login(token!!.accessToken, "KAKAO")
        }.handleResult(token, error)
    }

    fun kakaoLogin() = viewModelScope.launch {
        kakaoLoginRepository.startKakaoLogin(kakaoLoginCallback)
    }

    fun login(socialToken: String, socialType: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            authRepository.postLogin(socialToken, RequestLoginDto(socialType))
                .onSuccess { response ->
                    Timber.e("로그인 성공")
                    _loginState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                    _loginState.value = UiState.Failure("${t.message}")
                }
        }
    }

}
