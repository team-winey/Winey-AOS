package com.android.go.sopt.winey.data.service

import com.kakao.sdk.auth.model.OAuthToken

interface KakaoLoginService {
    fun startKakaoLogin(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit)
    fun kakaoLogout(kakaoLogoutCallBack: (Throwable?) -> Unit)
    fun kakaoDeleteAccount(kakaoLogoutCallBack: (Throwable?) -> Unit)
}
