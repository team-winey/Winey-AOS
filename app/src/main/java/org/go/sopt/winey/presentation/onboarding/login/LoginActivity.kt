package org.go.sopt.winey.presentation.onboarding.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityLoginBinding
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.onboarding.story.StoryActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.amplitude.type.EventType
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.view.UiState
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity :
    BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendEventToAmplitude(EventType.TYPE_VIEW_SCREEN)

        initKakaoLoginButtonClickListener()
        initLoginObserver()
    }

    private fun initKakaoLoginButtonClickListener() {
        binding.btnLoginKakao.setOnClickListener {
            sendEventToAmplitude(EventType.TYPE_CLICK_BUTTON)
            viewModel.loginKakao(this)
        }
    }

    private fun sendEventToAmplitude(type: EventType) {
        val eventProperties = JSONObject()

        try {
            when (type) {
                EventType.TYPE_VIEW_SCREEN -> eventProperties.put("screen_name", "sign_up")
                EventType.TYPE_CLICK_BUTTON -> {
                    eventProperties.put("button_name", "kakao_signup_button")
                        .put("paging_number", 1)
                }
                else -> {}
            }
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        when (type) {
            EventType.TYPE_VIEW_SCREEN -> amplitudeUtils.logEvent("view_signup", eventProperties)
            EventType.TYPE_CLICK_BUTTON -> amplitudeUtils.logEvent("click_button", eventProperties)
            else -> {}
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
