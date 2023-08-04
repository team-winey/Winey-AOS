package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.source.KakaoLoginDataSource
import com.android.go.sopt.winey.domain.repository.KakaoLoginRepository
import com.kakao.sdk.auth.model.OAuthToken
import javax.inject.Inject

class KakaoLoginRepositoryImpl @Inject constructor(
    private val kakaoLoginDataSource: KakaoLoginDataSource
) : KakaoLoginRepository {
    override fun startKakaoLogin(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit) {
        kakaoLoginDataSource.startKakaoLogin(kakaoLoginCallBack)
    }

    override fun kakaoLogout(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        kakaoLoginDataSource.kakaoLogout(kakaoLogoutCallBack)
    }

    override fun kakaoDeleteAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        kakaoLoginDataSource.kakaoDeleteAccount(kakaoLogoutCallBack)
    }
}
