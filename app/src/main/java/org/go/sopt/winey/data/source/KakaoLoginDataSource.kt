package org.go.sopt.winey.data.source

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import org.go.sopt.winey.data.service.KakaoLoginService
import javax.inject.Inject

class KakaoLoginDataSource @Inject constructor(
    private val client: UserApiClient
) : KakaoLoginService {

    override fun loginKakao(
        kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit,
        context: Context
    ) {
        val kakaoLoginState =
            if (client.isKakaoTalkLoginAvailable(context)) {
                KAKAO_TALK_LOGIN
            } else {
                KAKAO_ACCOUNT_LOGIN
            }

        when (kakaoLoginState) {
            KAKAO_TALK_LOGIN -> {
                client.loginWithKakaoTalk(
                    context,
                    callback = kakaoLoginCallBack
                )
            }

            KAKAO_ACCOUNT_LOGIN -> {
                client.loginWithKakaoAccount(
                    context,
                    callback = kakaoLoginCallBack
                )
            }
        }
    }

    override fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        client.logout(kakaoLogoutCallBack)
    }

    override fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) {
        client.unlink(kakaoLogoutCallBack)
    }

    companion object {
        private const val KAKAO_TALK_LOGIN = 0
        private const val KAKAO_ACCOUNT_LOGIN = 1
    }
}
