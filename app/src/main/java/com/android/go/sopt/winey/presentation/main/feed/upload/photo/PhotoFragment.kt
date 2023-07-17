package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.content.ContentFragment
import com.android.go.sopt.winey.util.binding.BindingFragment

class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {
    private val viewModel by viewModels<PhotoViewModel>()
    private lateinit var imageUriArg: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPhotoUploadButtonClickListener()
        initNextButtonClickListener()
        initCloseButtonClickListener()
    }

    private fun initPhotoUploadButtonClickListener() {
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                if (imageUri == null) {
                    displayErrorImage()
                    return@registerForActivityResult
                }

                initImageUriString(imageUri)
                displayImage()
                viewModel.apply {
                    updateImageUri(imageUri)
                    activateNextButton()
                }
            }

        binding.ivUploadPhoto.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun displayErrorImage() {
        binding.ivUploadPhoto.setImageResource(R.drawable.ic_upload_image_error)
    }

    private fun initImageUriString(imageUri: Uri) {
        imageUriArg = imageUri.toString()
    }

    private fun displayImage() {
        binding.ivUploadPhoto.load(imageUriArg) {
            transformations(RoundedCornersTransformation(20f))
        }
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
                    putString(PHOTO_KEY, imageUriArg)
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