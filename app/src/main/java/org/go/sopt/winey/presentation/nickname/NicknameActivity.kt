package org.go.sopt.winey.presentation.nickname

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityNicknameBinding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.code.ErrorCode
import org.go.sopt.winey.util.context.hideKeyboard
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.view.InputUiState
import org.go.sopt.winey.util.view.UiState
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NicknameActivity : BindingActivity<ActivityNicknameBinding>(R.layout.activity_nickname) {
    private val viewModel by viewModels<NicknameViewModel>()
    private val prevScreenName by lazy { intent.extras?.getString(KEY_PREV_SCREEN_NAME, "") }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amplitudeUtils.logEvent("view_set_nickname")
        binding.vm = viewModel
        viewModel.updatePrevScreenName(prevScreenName)

        initRootLayoutClickListener()
        initCloseButtonClickListener()
        switchEditTextHint()

        initEditTextWatcher()
        initDuplicateCheckButtonClickListener()
        initCompleteButtonClickListener()
        initPatchNicknameStateObserver()
    }

    private fun initEditTextWatcher() {
        var prevText = ""
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            // 텍스트가 바뀌면 중복체크 상태 false로 초기화
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()

                if (inputText.isNotBlank() && inputText != prevText) {
                    viewModel.updateDuplicateCheckState(false)
                    Timber.d("DUPLICATE CHECK: ${viewModel.duplicateChecked.value}")
                }

                prevText = inputText
            }
        })
    }

    private fun initDuplicateCheckButtonClickListener() {
        binding.btnNicknameDuplicateCheck.setOnClickListener {
            viewModel.apply {
                if (checkValidInput()) {
                    getNicknameDuplicateCheck()
                }
            }
        }
    }

    private fun initCompleteButtonClickListener() {
        binding.btnNicknameComplete.setOnClickListener {
            // 중복체크 하지 않고 시작하기 버튼 누르면 에러 표시
            if (!viewModel.duplicateChecked.value) {
                viewModel.updateInputUiState(
                    InputUiState.Failure(ErrorCode.CODE_UNCHECKED_DUPLICATION)
                )
                return@setOnClickListener
            }

            // 유효한 닉네임인 경우에만 PATCH 서버통신 진행
            if (viewModel.isValidNickname.value) {
                sendEventToAmplitude()
                viewModel.patchNickname()
            }
        }
    }

    private fun initPatchNicknameStateObserver() {
        viewModel.patchNicknameState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Loading -> preventContinuousButtonClick()
                    is UiState.Success -> {
                        when (prevScreenName) {
                            STORY_SCREEN -> navigateTo<MainActivity>()
                            MY_PAGE_SCREEN -> finish() // 마이페이지 onStart에서 유저 데이터 갱신
                        }
                    }

                    is UiState.Failure -> snackBar(binding.root) { state.msg }
                    is UiState.Empty -> {}
                }
            }.launchIn(lifecycleScope)
    }

    private fun preventContinuousButtonClick() {
        binding.btnNicknameComplete.isClickable = false
    }

    private fun switchEditTextHint() {
        when (prevScreenName) {
            STORY_SCREEN -> {
                binding.etNickname.hint = stringOf(R.string.nickname_default_hint)
            }

            MY_PAGE_SCREEN -> {
                lifecycleScope.launch {
                    val user = dataStoreRepository.getUserInfo().first() ?: return@launch
                    binding.etNickname.hint = user.nickname
                    binding.originalNicknameLength = user.nickname.length
                }
            }
        }
    }

    private fun initCloseButtonClickListener() {
        binding.ivNicknameClose.setOnClickListener {
            finish()
        }
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
            binding.etNickname.clearFocus()
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun sendEventToAmplitude() {
        val eventProperties = JSONObject()
        try {
            eventProperties.put("button_name", "nickname_next_button")
                .put("paging_number", 1)
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }
        amplitudeUtils.logEvent("click_button", eventProperties)
    }

    companion object {
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        const val MY_PAGE_SCREEN = "MyPageFragment"
        const val STORY_SCREEN = "StoryActivity"
    }
}
