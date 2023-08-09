package com.android.go.sopt.winey.data.repository

import android.content.Context
import com.android.go.sopt.winey.data.source.KakaoLoginDataSource
import com.android.go.sopt.winey.domain.repository.KakaoLoginRepository
import com.kakao.sdk.auth.model.OAuthToken
import javax.inject.Inject

class KakaoLoginRepositoryImpl @Inject constructor(
    private val kakaoLoginDataSource: KakaoLoginDataSource
) : KakaoLoginRepository {
    override fun loginKakao(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit, context: Context) {
        kakaoLoginDataSource.loginKakao(kakaoLoginCallBack, context)
    }

    override fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        kakaoLoginDataSource.logoutKakao(kakaoLogoutCallBack)
    }

    override fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        kakaoLoginDataSource.deleteKakaoAccount(kakaoLogoutCallBack)
    }
}
