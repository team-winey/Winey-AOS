package com.android.go.sopt.winey.presentation.onboarding

import android.os.Bundle
import android.util.Log
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityOnBoardingBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class OnBoardingActivity: BindingActivity<ActivityOnBoardingBinding>(R.layout.activity_on_boarding) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnOnboardingKakao.setOnClickListener {

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오계정으로 로그인 성공 액세스토큰 ${token.accessToken}")
                    Log.i("LOGIN", "카카오계정으로 로그인 성공 리프레시토큰 ${token.refreshToken}")
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("LOGIN", "카카오톡으로 로그인 성공 액세스토큰 ${token.accessToken}")
                        Log.i("LOGIN", "카카오톡으로 로그인 성공 리프레시토큰 ${token.refreshToken}")

                        finish()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}
