package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentPhotoBinding
import com.android.go.sopt.winey.util.amplitude.AmplitudeUtils
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.WineyDialogFragment
import com.android.go.sopt.winey.util.fragment.stringOf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class PhotoFragment : BindingFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {
    private val uploadViewModel by activityViewModels<UploadViewModel>()

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_upload")
        binding.vm = uploadViewModel

        initImageSelectButtonClickListener()
        initNextButtonClickListener()
        initCloseButtonClickListener()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showAlertDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.upload_dialog_title),
            stringOf(R.string.upload_dialog_subtitle),
            stringOf(R.string.upload_dialog_negative_button),
            stringOf(R.string.upload_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { requireActivity().finish() }
        )
        dialog.show(parentFragmentManager, TAG_UPLOAD_DIALOG)
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
            sendEventToAmplitude()
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

    private fun sendEventToAmplitude() {
        val eventProperties = JSONObject()

        try {
            eventProperties.put("button_name", "upload_next_button")
                .put("paging_number", 2)
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        amplitudeUtils.logEvent("click_button", eventProperties)
    }

    companion object {
        private const val IMAGE_FILE = "image/*"
        private const val TAG_UPLOAD_DIALOG = "UPLOAD_DIALOG"
    }
}
