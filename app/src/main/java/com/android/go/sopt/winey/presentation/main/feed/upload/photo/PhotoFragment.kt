package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.UploadViewModel
import com.android.go.sopt.winey.presentation.main.feed.upload.content.ContentFragment
import com.android.go.sopt.winey.util.binding.BindingFragment

class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {
    private val uploadViewModel by activityViewModels<UploadViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = uploadViewModel

        initImageSelectButtonClickListener()
        initNextButtonClickListener()
        initCloseButtonClickListener()
    }

    private fun initImageSelectButtonClickListener() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                if (imageUri == null) {
                    showDefaultImage()
                    return@registerForActivityResult
                }

                uploadViewModel.apply {
                    updateImageUri(imageUri)
                    activateNextButton()
                }
            }

        binding.ivUploadPhoto.setOnClickListener {
            launcher.launch(IMAGE_FILE)
        }
    }

    private fun showDefaultImage() {
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
            replace(R.id.fcv_upload, ContentFragment())
            addToBackStack(null)
        }
    }

    companion object {
        private const val ARGS_PHOTO_KEY = "photo"
        private const val IMAGE_FILE = "image/*"
    }
}
