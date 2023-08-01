package com.android.go.sopt.winey.presentation.main.feed.upload.content

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentContentBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.amount.AmountFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.hideKeyboard

class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    private val viewModel by viewModels<ContentViewModel>()
    private val imageUriArg by lazy { requireArguments().getParcelable<Uri>(PHOTO_KEY) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initRootLayoutClickListener()
        initNextButtonClickListener()
        initBackButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnContentNext.setOnClickListener {
            navigateToNext()
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivContentBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToNext() {
        parentFragmentManager.commit {
            val fragmentWithBundle = AmountFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PHOTO_KEY, imageUriArg)
                    putString(CONTENT_KEY, viewModel.content)
                }
            }
            replace(R.id.fcv_upload, fragmentWithBundle)
            addToBackStack(null)
        }
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            requireContext().hideKeyboard(binding.root)
            focusOutEditText()
        }
    }

    private fun focusOutEditText() {
        binding.etUploadContent.clearFocus()
    }

    companion object {
        private const val PHOTO_KEY = "photo"
        private const val CONTENT_KEY = "content"
    }
}
