package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentAmountBinding
import com.android.go.sopt.winey.util.BitmapRequestBody
import com.android.go.sopt.winey.util.ImageCompressor
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.hideKeyboard
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat

@AndroidEntryPoint
class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val viewModel by viewModels<AmountViewModel>()
    private val imageUriArg by lazy { requireArguments().getParcelable<Uri>(PHOTO_KEY) }
    private val contentArg by lazy { requireArguments().getString(CONTENT_KEY, "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        setImageRequestBody()
        initPostImageStateObserver()
        initUploadButtonClickListener()

        initRootLayoutClickListener()
        initBackButtonClickListener()
        initEditTextWatcher()
    }

    private fun setImageRequestBody() {
        if (imageUriArg == null) {
            Timber.e("Image Uri Argument is null")
            return
        }
        viewModel.setImageRequestBody(getBitmapRequestBody())
    }

    private fun getBitmapRequestBody(): BitmapRequestBody {
        val compressor = ImageCompressor(requireContext(), imageUriArg!!)
        val adjustedImageBitmap = compressor.adjustImageFormat()
        return BitmapRequestBody(requireContext(), imageUriArg, adjustedImageBitmap)
    }

    private fun initUploadButtonClickListener() {
        binding.btnAmountNext.setOnSingleClickListener {
            val amountWithoutComma = viewModel.amount.replace(",", "")
            viewModel.postWineyFeed(contentArg, amountWithoutComma)
        }
    }

    private fun initPostImageStateObserver() {
        viewModel.postWineyFeedState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // todo: 입력된 금액 범위에 따라 텍스트 변경
                }

                is UiState.Success -> {
                    snackBar(binding.root) { "${state.data?.feedId} ${state.data?.createdAt}" }
                    closeUploadScreen()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {

                }
            }
        }
    }

    private fun closeUploadScreen() {
        requireActivity().finish()
    }

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
        private const val PHOTO_KEY = "photo"
        private const val CONTENT_KEY = "content"
    }
}