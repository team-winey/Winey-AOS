package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.os.Bundle
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