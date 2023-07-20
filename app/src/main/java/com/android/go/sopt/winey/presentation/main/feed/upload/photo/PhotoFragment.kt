package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.content.ContentFragment
import com.android.go.sopt.winey.util.binding.BindingFragment

class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {
    private val viewModel by viewModels<PhotoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPhotoUploadButtonClickListener()
        initNextButtonClickListener()
        initCloseButtonClickListener()
    }

    private fun initPhotoUploadButtonClickListener() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                if (imageUri == null) {
                    displayDefaultImage()
                    return@registerForActivityResult
                }

                viewModel.apply {
                    updatePhotoSelectState() // 버튼 활성화를 위해서
                    updateImageUri(imageUri) // 바인딩 어댑터 적용
                }
            }

        binding.ivUploadPhoto.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    private fun displayDefaultImage() {
        binding.ivUploadPhoto.setImageResource(R.drawable.img_upload_photo)
    }

    private fun initNextButtonClickListener() {
        binding.btnPhotoNext.setOnClickListener {
            navigateToNext()
        }
    }

    private fun initCloseButtonClickListener() {
        binding.ivPhotoClose.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun navigateToNext() {
        parentFragmentManager.commit {
            val fragmentWithBundle = ContentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PHOTO_KEY, viewModel.imageUri.value)
                }
            }
            replace(R.id.fcv_upload, fragmentWithBundle)
            addToBackStack(null)
        }
    }

    companion object {
        private const val PHOTO_KEY = "photo"
    }
}