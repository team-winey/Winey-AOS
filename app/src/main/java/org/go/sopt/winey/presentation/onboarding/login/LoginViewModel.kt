package org.go.sopt.winey.presentation.onboarding.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.go.sopt.winey.data.model.remote.request.RequestLoginDto
import org.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.domain.repository.KakaoLoginRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.util.view.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val kakaoLoginRepository: KakaoLoginRepository,
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<ResponseLoginDto?>>(UiState.Empty)
    val loginState: StateFlow<UiState<ResponseLoginDto?>> = _loginState.asStateFlow()

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        KakaoLoginCallback {
            Timber.d("액세스토큰 ${token?.accessToken}")
            Timber.d("리프레시토큰 ${token?.refreshToken}")
            if (token != null) {
                saveSocialToken(token.accessToken, token.refreshToken)
                postLogin(token.accessToken, KAKAO)
            } else {
                Timber.e("token is null")
            }
        }.handleResult(token, error)
    }

    fun loginKakao(context: Context) = viewModelScope.launch {
        kakaoLoginRepository.loginKakao(kakaoLoginCallback, context)
    }

    private fun postLogin(socialToken: String, socialType: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            authRepository.postLogin(socialToken, RequestLoginDto(socialType))
                .onSuccess { response ->
                    if (response != null) {
                        Timber.d("로그인 성공")
                        Timber.d("액세스 : ${response.accessToken} \n 리프레시 : ${response.refreshToken}")

                        saveAccessToken(response.accessToken, response.refreshToken)
                        saveUserId(response.userId)

                        _loginState.value = UiState.Success(response)
                    } else {
                        Timber.e("response is null")
                    }
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패 ${t.code()}, ${t.message()}")
                    }
                    Timber.e("${t.message}")
                    _loginState.value = UiState.Failure("${t.message}")
                }
        }
    }

    private fun saveSocialToken(socialAccessToken: String, socialRefreshToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSocialToken(socialAccessToken, socialRefreshToken)
        }

    private fun saveAccessToken(accessToken: String, refreshToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveAccessToken(accessToken, refreshToken)
        }

    private fun saveUserId(userId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveUserId(userId)
        }

    companion object {
        private const val KAKAO = "KAKAO"
    }
}
