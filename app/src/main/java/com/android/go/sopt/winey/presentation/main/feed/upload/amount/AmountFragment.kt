package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentAmountBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.loading.LoadingActivity
import com.android.go.sopt.winey.util.UriToRequestBody
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.hideKeyboard
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val viewModel by viewModels<AmountViewModel>()
    private val imageUriArg by lazy { requireArguments().getParcelable<Uri>(ARGS_PHOTO_KEY) }
    private val contentArg by lazy { requireArguments().getString(ARGS_CONTENT_KEY, "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        updateRequestBody()
        initPostImageStateObserver()
        initUploadButtonClickListener()

        initRootLayoutClickListener()
        initBackButtonClickListener()
        initEditTextWatcher()
    }

    private fun updateRequestBody() {
//        val compressor = ImageCompressor(requireContext(), imageUriArg!!)
//        val adjustedImageBitmap = compressor.adjustImageFormat()
//        val bitmapRequestBody =
//            BitmapRequestBody(requireContext(), imageUriArg, adjustedImageBitmap)

        val requestBody = UriToRequestBody(requireContext(), imageUriArg!!)
        viewModel.updateRequestBody(requestBody)
    }

    private fun initUploadButtonClickListener() {
        binding.btnAmountNext.setOnSingleClickListener {
            viewModel.postWineyFeed(contentArg, viewModel.amount.removeComma())
        }
    }

    private fun initPostImageStateObserver() {
        viewModel.postWineyFeedState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    navigateLoadingScreen()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {

                }
            }
        }
    }

    private fun navigateLoadingScreen() {
        Intent(requireContext(), LoadingActivity::class.java).apply {
            putExtra(EXTRA_AMOUNT_KEY, viewModel.amount.removeComma())
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun String.removeComma() = replace(",", "")

    private fun initBackButtonClickListener() {
        binding.ivAmountBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initEditTextWatcher() {
        var temp = ""
        binding.etUploadAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()

                // 문자열이 비어있거나 이전과 변함 없으면 그대로 둔다.
                if (input.isBlank() || input == temp) return

                temp = makeCommaString(input.replace(",", "").toLong())
                binding.etUploadAmount.apply {
                    setText(temp)
                    setSelection(temp.length) // 커서를 오른쪽 끝으로 보낸다.
                }
            }
        })
    }

    private fun makeCommaString(input: Long): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(input)
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            requireContext().hideKeyboard(binding.root)
            focusOutEditText()
        }
    }

    private fun focusOutEditText() {
        binding.etUploadAmount.clearFocus()
    }

    companion object {
        private const val ARGS_PHOTO_KEY = "photo"
        private const val ARGS_CONTENT_KEY = "content"
        private const val EXTRA_AMOUNT_KEY = "amount"
    }
}