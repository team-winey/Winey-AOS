package com.android.go.sopt.winey.presentation.onboarding.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityLoginBinding
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.presentation.onboarding.story.StoryActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity :
    BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKakaoLoginButtonClickListener()
        initLoginObserver()
    }

    private fun initKakaoLoginButtonClickListener() {
        binding.btnLoginKakao.setOnClickListener {
            viewModel.loginKakao(this)
        }
    }

    private fun initLoginObserver() {
        viewModel.loginState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnLoginKakao.isEnabled = false
                }

                is UiState.Success -> {
                    Timber.e("${state.data?.userId}")
                    Timber.e("${state.data?.accessToken}")
                    Timber.e("${state.data?.refreshToken}")
                    Timber.e("${state.data?.isRegistered}")

                    if (state.data?.isRegistered == true) {
                        navigateTo<MainActivity>()
                    } else {
                        navigateTo<StoryActivity>()
                    }
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this@LoginActivity, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }
}
