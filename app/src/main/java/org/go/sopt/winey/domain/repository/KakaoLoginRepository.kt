package org.go.sopt.winey.domain.repository

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken

interface KakaoLoginRepository {
    fun loginKakao(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit, context: Context)

    fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit)

    fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit)
}
