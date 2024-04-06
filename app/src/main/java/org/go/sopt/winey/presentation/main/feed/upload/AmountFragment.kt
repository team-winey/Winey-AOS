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
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.main.feed.upload.loading.LoadingActivity
import org.go.sopt.winey.presentation.model.WineyFeedType
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.context.hideKeyboard
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.fragment.wineySnackbar
import org.go.sopt.winey.util.multipart.UriToRequestBody
import org.go.sopt.winey.util.number.formatAmountNumber
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.setOnSingleClickListener
import org.go.sopt.winey.util.view.snackbar.SnackbarType

@AndroidEntryPoint
class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val uploadViewModel by activityViewModels<UploadViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = uploadViewModel

        updateRequestBody()
        initListener()
        initObserver()
    }

    private fun updateRequestBody() {
        val imageUri = uploadViewModel.imageUri.value ?: return
        val requestBody = UriToRequestBody(requireContext(), imageUri)
        uploadViewModel.updateRequestBody(requestBody)
    }

    private fun initListener() {
        initAmountTextChangedListener()
        initUploadButtonClickListener()
        initRootLayoutClickListener()
        initBackButtonClickListener()
    }

    private fun initObserver() {
        initPostImageStateObserver()
    }

    /** Listener */
    private fun initAmountTextChangedListener() {
        var prevAmount = ""
        binding.etUploadAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentAmount = s.toString()

                // 문자열이 비어있거나 이전과 변함 없으면 그대로 둔다.
                if (currentAmount.isBlank() || currentAmount == prevAmount) return

                val newAmount = currentAmount.removeComma().toInt()
                prevAmount = newAmount.formatAmountNumber()

                binding.etUploadAmount.apply {
                    setText(prevAmount)

                    // 커서를 오른쪽 끝으로 보낸다.
                    setSelection(prevAmount.length)
                }
            }
        })
    }

    private fun initUploadButtonClickListener() {
        binding.btnAmountNext.setOnSingleClickListener {
            uploadViewModel.apply {
                postWineyFeed(
                    content = content,
                    amount = commaAmount.removeComma(),
                    feedType = feedType.name
                )
            }
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivAmountBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            context?.hideKeyboard(binding.root)
            binding.etUploadAmount.clearFocus()
        }
    }

    /** Observer */
    private fun initPostImageStateObserver() {
        uploadViewModel.postWineyFeedState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Loading -> {
                        preventUploadButtonClick()
                    }

                    is UiState.Success -> {
                        val response = state.data ?: return@onEach
                        navigateToLoadingScreen(response.levelUpgraded)
                    }

                    is UiState.Failure -> {
                        wineySnackbar(
                            anchorView = binding.root,
                            message = stringOf(R.string.snackbar_upload_fail),
                            type = SnackbarType.WineyFeedResult(isSuccess = false)
                        )
                        uploadViewModel.initPostWineyFeedState()
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun preventUploadButtonClick() {
        binding.btnAmountNext.isClickable = false
    }

    private fun navigateToLoadingScreen(nowLevelUp: Boolean) {
        val context = context ?: return
        Intent(context, LoadingActivity::class.java).apply {
            putExtra(KEY_SAVE_AMOUNT, uploadViewModel.commaAmount.removeComma())
            putExtra(WineyFeedFragment.KEY_FEED_TYPE, uploadViewModel.feedType)
            if (uploadViewModel.feedType == WineyFeedType.SAVE) {
                putExtra(WineyFeedFragment.KEY_LEVEL_UP, nowLevelUp)
            }
            startActivity(this)
        }
    }

    private fun String.removeComma() = replace(",", "")

    companion object {
        const val KEY_SAVE_AMOUNT = "amount"
    }
}
