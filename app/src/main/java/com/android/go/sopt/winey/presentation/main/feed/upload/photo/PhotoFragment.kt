package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.content.ContentFragment
import com.android.go.sopt.winey.util.binding.BindingFragment

class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {
    private val viewModel by viewModels<PhotoViewModel>()
    private lateinit var imageUriBundleValue: String

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

                imageUriBundleValue = imageUri.toString()
                uploadPhoto(imageUri)
                viewModel.activateNextButton()
            }

        binding.ivUploadPhoto.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun displayErrorImage() {
        binding.ivUploadPhoto.setImageResource(R.drawable.ic_upload_image_error)
    }

    private fun uploadPhoto(imageUri: Uri) {
        binding.ivUploadPhoto.load(imageUri) {
            transformations(RoundedCornersTransformation(20f))
        }
    }

    private fun initNextButtonClickListener() {
        binding.btnPhotoNext.setOnClickListener {
            navigateWithBundle<ContentFragment>(imageUriBundleValue)
        }
    }

    private fun initCloseButtonClickListener() {
        binding.ivPhotoClose.setOnClickListener {
            requireActivity().finish()
        }
    }

    private inline fun <reified T : Fragment> navigateWithBundle(imageUri: String) {
        parentFragmentManager.commit {
            replace<T>(
                R.id.fcv_upload,
                T::class.simpleName,
                Bundle().apply { putString(PHOTO_KEY, imageUri) }
            )
        }
    }

    companion object {
        private const val PHOTO_KEY = "photo"
    }
}