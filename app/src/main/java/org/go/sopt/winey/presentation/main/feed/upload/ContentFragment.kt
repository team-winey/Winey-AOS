package org.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentContentBinding
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.context.hideKeyboard
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    private val uploadViewModel by activityViewModels<UploadViewModel>()

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = uploadViewModel

        initRootLayoutClickListener()
        initNextButtonClickListener()
        initBackButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnContentNext.setOnClickListener {
            sendEventToAmplitude()
            navigateToNext()
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivContentBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToNext() {
        parentFragmentManager.commit {
            replace(R.id.fcv_upload, AmountFragment())
            addToBackStack(null)
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

    private fun sendEventToAmplitude() {
        val eventProperties = JSONObject()

        try {
            eventProperties.put("button_name", "upload_next_button")
                .put("paging_number", 3)
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        amplitudeUtils.logEvent("click_button", eventProperties)
    }
}
