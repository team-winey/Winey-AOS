package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.service.KakaoLoginService
import com.kakao.sdk.auth.model.OAuthToken
import javax.inject.Inject

class KakaoLoginDataSource @Inject constructor(
    private val kakaoLoginService: KakaoLoginService
) {
    fun startKakaoLogin(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit) =
        kakaoLoginService.startKakaoLogin(kakaoLoginCallBack)

    fun kakaoLogout(kakaoLogoutCallBack: (Throwable?) -> Unit) =
        kakaoLoginService.kakaoLogout(kakaoLogoutCallBack)

    fun kakaoDeleteAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) =
        kakaoLoginService.kakaoDeleteAccount(kakaoLogoutCallBack)
}
