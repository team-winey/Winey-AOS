package com.android.go.sopt.winey.presentation.main.feed.upload.content

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentContentBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.amount.AmountFragment
import com.android.go.sopt.winey.presentation.main.feed.upload.photo.PhotoFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.hideKeyboard

class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    private val viewModel by viewModels<ContentViewModel>()
    private val imageUri by lazy { requireArguments().getString(PHOTO_KEY, "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initRootLayoutClickListener()
        initNextButtonClickListener()
        initBackButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnContentNext.setOnClickListener {
            navigateWithBundle<AmountFragment>(imageUri, viewModel.content)
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivContentBack.setOnClickListener {
            navigateTo<PhotoFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }

    private inline fun <reified T : Fragment> navigateWithBundle(
        imageUri: String,
        content: String
    ) {
        parentFragmentManager.commit {
            replace<T>(
                R.id.fcv_upload,
                T::class.simpleName,
                Bundle().apply {
                    putString(PHOTO_KEY, imageUri)
                    putString(CONTENT_KEY, content)
                }
            )
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