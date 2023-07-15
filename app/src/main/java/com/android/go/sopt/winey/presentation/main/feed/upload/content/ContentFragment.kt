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

class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    private val viewModel by viewModels<ContentViewModel>()
    private val imageUri by lazy { requireArguments().getString(PHOTO_KEY, "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initNextButtonClickListener()
        initBackButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnContentNext.setOnClickListener {
            navigateWithBundle<AmountFragment>(imageUri.toString(), viewModel.content)
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivContentBack.setOnClickListener {
            navigateTo<PhotoFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        requireActivity().supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }

    private inline fun <reified T : Fragment> navigateWithBundle(
        imageUri: String,
        content: String
    ) {
        requireActivity().supportFragmentManager.commit {
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

    companion object {
        private const val PHOTO_KEY = "photo"
        private const val CONTENT_KEY = "content"
    }
}