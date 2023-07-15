package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentAmountBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.content.ContentFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.hideKeyboard
import com.android.go.sopt.winey.util.fragment.toast
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import java.math.BigInteger
import java.text.DecimalFormat

class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val viewModel by viewModels<AmountViewModel>()
    private val imageUri by lazy { requireArguments().getString(PHOTO_KEY, "") }
    private val content by lazy { requireArguments().getString(CONTENT_KEY, "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initRootLayoutClickListener()
        initBackButtonClickListener()
        initUploadButtonClickListener()
        initEditTextWatcher()
    }

    private fun initBackButtonClickListener() {
        binding.ivAmountBack.setOnClickListener {
            navigateTo<ContentFragment>()
        }
    }

    // todo: imageUri, content, viewModel.amount -> 멀티파트 서버통신
    private fun initUploadButtonClickListener() {
        binding.btnAmountNext.setOnSingleClickListener {
            toast("$content ${viewModel.amount}")
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

                temp = makeCommaNumber(input.replace(",", "").toBigInteger())
                binding.etUploadAmount.apply {
                    setText(temp)
                    setSelection(temp.length) // 커서를 오른쪽 끝으로 보낸다.
                }
            }
        })
    }

    private fun makeCommaNumber(input: BigInteger): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(input)
    }

    private inline fun <reified T : Fragment> navigateTo() {
        requireActivity().supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
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