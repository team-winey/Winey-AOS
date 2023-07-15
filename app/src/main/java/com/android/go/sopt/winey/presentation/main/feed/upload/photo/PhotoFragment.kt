package com.android.go.sopt.winey.presentation.main.feed.upload.photo

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
                binding.ivUploadPhoto.load(imageUri) {
                    error(R.drawable.ic_upload_image_error)
                    transformations(RoundedCornersTransformation(20f))
                }
                viewModel.activateNextButton()
            }

        binding.ivUploadPhoto.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun initNextButtonClickListener() {
        binding.btnUploadPhotoNext.setOnClickListener {
            navigateTo<ContentFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        requireActivity().supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }
}