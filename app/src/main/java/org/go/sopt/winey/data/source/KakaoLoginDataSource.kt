package org.go.sopt.winey.data.source

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import org.go.sopt.winey.data.service.KakaoLoginService
import javax.inject.Inject

class KakaoLoginDataSource @Inject constructor(
    private val kakaoLoginService: KakaoLoginService
) {
    fun loginKakao(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit, context: Context) =
        kakaoLoginService.loginKakao(kakaoLoginCallBack, context)

    fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit) =
        kakaoLoginService.logoutKakao(kakaoLogoutCallBack)

    fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) =
        kakaoLoginService.deleteKakaoAccount(kakaoLogoutCallBack)
}
