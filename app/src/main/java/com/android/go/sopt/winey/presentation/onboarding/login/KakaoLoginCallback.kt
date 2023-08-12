package com.android.go.sopt.winey.presentation.onboarding.login

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import timber.log.Timber

class KakaoLoginCallback(private val onSuccess: (accessToken: String) -> Unit) {
    fun handleResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            when {
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Timber.e(error, ACCESS_DENIED)
                }

                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Timber.e(error, INVALID_CLIENT)
                }

                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Timber.e(error, INVALID_GRANT)
                }

                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Timber.e(error, INVALID_REQUEST)
                }

                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Timber.e(error, INVALID_SCOPE)
                }

                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Timber.e(error, MISCONFIGURED)
                }

                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Timber.e(error, SERVER_ERROR)
                }

                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Timber.e(error, UNAUTHORIZED)
                }

                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    Timber.e(error, CANCELLED)
                }

                else -> { //
                    Timber.e(error, ELSE)
                }
            }
        } else if (token != null) {
            Timber.d(ON_SUCCESS)
            onSuccess(token.accessToken)
        }
    }
    companion object {
        private const val ACCESS_DENIED = "접근이 거부 됨(동의 취소)"
        private const val INVALID_CLIENT = "유효하지 않은 앱"
        private const val INVALID_GRANT = "인증 수단이 유효하지 않아 인증할 수 없는 상태"
        private const val INVALID_REQUEST = "요청 파라미터 오류"
        private const val INVALID_SCOPE = "유효하지 않은 scope ID"
        private const val MISCONFIGURED = "설정이 올바르지 않음(android key hash)"
        private const val SERVER_ERROR = "서버 내부 에러"
        private const val UNAUTHORIZED = "앱이 요청 권한이 없음"
        private const val CANCELLED = "의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리"
        private const val ELSE = "기타 에러"
        private const val ON_SUCCESS = "로그인 성공"
    }
}
