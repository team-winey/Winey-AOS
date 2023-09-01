package org.go.sopt.winey.presentation.main.feed.upload

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentAmountBinding
import org.go.sopt.winey.presentation.main.feed.upload.loading.LoadingActivity
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.context.hideKeyboard
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.fragment.wineySnackbar
import org.go.sopt.winey.util.multipart.UriToRequestBody
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.setOnSingleClickListener
import java.text.DecimalFormat

@AndroidEntryPoint
class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val uploadViewModel by activityViewModels<UploadViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = uploadViewModel

        updateRequestBody()
        initPostImageStateObserver()
        initUploadButtonClickListener()

        initRootLayoutClickListener()
        initBackButtonClickListener()
        initEditTextWatcher()
    }

    private fun updateRequestBody() {
        val imageUri = uploadViewModel.imageUri.value ?: return
        val requestBody = UriToRequestBody(requireContext(), imageUri)
        uploadViewModel.updateRequestBody(requestBody)
    }

    private fun initUploadButtonClickListener() {
        binding.btnAmountNext.setOnSingleClickListener {
            uploadViewModel.postWineyFeed(uploadViewModel.content, uploadViewModel.amount.removeComma())
        }
    }

    private fun initPostImageStateObserver() {
        uploadViewModel.postWineyFeedState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Loading -> {
                        preventUploadButtonClick()
                    }

                    is UiState.Success -> {
                        navigateLoadingScreen()
                    }

                    is UiState.Failure -> {
                        wineySnackbar(binding.root, false, stringOf(R.string.snackbar_upload_fail))
                    }

                    is UiState.Empty -> {
                    }
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun preventUploadButtonClick() {
        binding.btnAmountNext.isClickable = false
    }

    private fun navigateLoadingScreen() {
        Intent(requireContext(), LoadingActivity::class.java).apply {
            putExtra(EXTRA_AMOUNT_KEY, uploadViewModel.amount.removeComma())
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
        private const val EXTRA_AMOUNT_KEY = "amount"
    }
}
