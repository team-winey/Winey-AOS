package com.android.go.sopt.winey.domain.repository

import com.kakao.sdk.auth.model.OAuthToken

interface KakaoLoginRepository {
    fun startKakaoLogin(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit)

    fun kakaoLogout(kakaoLogoutCallBack: (Throwable?) -> Unit)

    fun kakaoDeleteAccount(kakaoLogoutCallBack: (Throwable?) -> Unit)
}
