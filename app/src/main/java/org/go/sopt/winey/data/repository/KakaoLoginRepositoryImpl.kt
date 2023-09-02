package org.go.sopt.winey.data.repository

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import org.go.sopt.winey.data.source.KakaoLoginDataSource
import org.go.sopt.winey.domain.repository.KakaoLoginRepository
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
