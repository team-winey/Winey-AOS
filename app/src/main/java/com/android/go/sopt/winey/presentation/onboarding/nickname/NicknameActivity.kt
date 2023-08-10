package com.android.go.sopt.winey.presentation.onboarding.nickname

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNicknameBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.colorOf
import com.android.go.sopt.winey.util.context.drawableOf
import com.android.go.sopt.winey.util.context.hideKeyboard
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NicknameActivity : BindingActivity<ActivityNicknameBinding>(R.layout.activity_nickname) {
    private val viewModel by viewModels<NicknameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        initRootLayoutClickListener()
        initEditTextWatcher()
        initDuplicateCheckButtonClickListener()
        initDuplicateCheckObserver()
    }

    private fun initDuplicateCheckObserver() {
        viewModel.getNicknameDuplicateCheckState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (state.data.isDuplicated) {
                            showErrorState()
                        } else {
                            showSuccessState()
                        }
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun showErrorState() {
        binding.apply {
            etNickname.background = drawableOf(R.drawable.shape_red_line_5_rect)
            tvNicknameHelperText.visibility = View.VISIBLE
            tvNicknameHelperText.text = stringOf(R.string.nickname_duplicate_error)
            tvNicknameHelperText.setTextColor(colorOf(R.color.red_500))
        }
    }

    private fun showSuccessState() {
        binding.apply {
            etNickname.background = drawableOf(R.drawable.shape_blue_line_5_rect)
            tvNicknameHelperText.visibility = View.VISIBLE
            tvNicknameHelperText.text = stringOf(R.string.nickname_valid)
            tvNicknameHelperText.setTextColor(colorOf(R.color.blue_500))
            btnNicknameComplete.isEnabled = true
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
                    updateDuplicateCheckButtonState(false)
                }
            }
        }
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
            binding.etNickname.clearFocus()
        }
    }
}
