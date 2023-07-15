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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPhotoUploadButtonClickListener()
        initNextButtonClickListener()
    }

    private fun initPhotoUploadButtonClickListener() {
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                if (imageUri == null) {
                    displayErrorImage()
                    return@registerForActivityResult
                }

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

    // todo: 다음 프래그먼트에 번들로 imageUri 넘기기
    private fun initNextButtonClickListener() {
        binding.btnPhotoNext.setOnClickListener {
            navigateTo<ContentFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        requireActivity().supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }
}