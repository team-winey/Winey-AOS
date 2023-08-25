package com.android.go.sopt.winey.presentation.nickname

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNicknameBinding
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.hideKeyboard
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NicknameActivity : BindingActivity<ActivityNicknameBinding>(R.layout.activity_nickname) {
    private val viewModel by viewModels<NicknameViewModel>()
    private val prevScreenName by lazy { intent.extras?.getString(EXTRA_KEY, "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.updatePrevScreenName(prevScreenName)

        initCloseButtonClickListener()
        initRootLayoutClickListener()
        initEditTextWatcher()
        initDuplicateCheckButtonClickListener()
        initPatchNicknameStateObserver()
    }

    private fun initCloseButtonClickListener() {
        binding.ivNicknameClose.setOnClickListener {
            finish()
        }
    }

    private fun initEditTextWatcher() {
        var prevText = ""
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                viewModel.updateTextChangedState(inputText.isNotBlank() && inputText != prevText)
                prevText = inputText
            }
        })
    }

    private fun initDuplicateCheckButtonClickListener() {
        binding.btnNicknameDuplicateCheck.setOnClickListener {
            if (viewModel.isTextChanged.value) {
                viewModel.apply {
                    updateDuplicateCheckButtonState(true)
                    getNicknameDuplicateCheck()
                }
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

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
            binding.etNickname.clearFocus()
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val EXTRA_KEY = "PREV_SCREEN_NAME"
        const val MY_PAGE_SCREEN = "MyPageFragment"
        const val STORY_SCREEN = "StoryActivity"
    }
}
