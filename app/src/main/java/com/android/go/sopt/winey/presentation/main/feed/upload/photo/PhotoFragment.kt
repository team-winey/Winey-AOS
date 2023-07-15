package com.android.go.sopt.winey.presentation.main.feed.upload.photo

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPhotoUploadButtonClickListener()
    }

    private fun initPhotoUploadButtonClickListener() {
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->

        }
    }
}