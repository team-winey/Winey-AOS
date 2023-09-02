package org.go.sopt.winey.data.service

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken

interface KakaoLoginService {
    fun loginKakao(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit, context: Context)
    fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit)
    fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit)
}
