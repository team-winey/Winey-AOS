package org.go.sopt.winey.util.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentUploadDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class WineyUploadDialogFragment :
    BindingDialogFragment<FragmentUploadDialogBinding>(R.layout.fragment_upload_dialog) {
    private var handleConsume: () -> Unit = {}
    private var handleSave: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
        setupDialogPosition()
        setupDialogBehavior()
    }

    private fun addListener() {
        initConsumeButtonClickListener(handleConsume)
        initSaveButtonClickListener(handleSave)
        initCloseButtonClickListener()
    }

    private fun setupDialogPosition() {
        dialog?.window?.let {
            val layoutParams = it.attributes
            layoutParams.gravity = Gravity.END or Gravity.BOTTOM
            it.attributes = layoutParams
        }
    }

    private fun setupDialogBehavior() {
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun initSaveButtonClickListener(handleSaveButton: () -> Unit) {
        binding.llWineyfeedFabSave.setOnClickListener {
            handleSaveButton.invoke()
            dismiss()
        }
    }

    private fun initConsumeButtonClickListener(handleConsumeButton: () -> Unit) {
        binding.llWineyfeedFabConsume.setOnClickListener {
            handleConsumeButton.invoke()
            dismiss()
        }
    }

    private fun initCloseButtonClickListener() {
        binding.ivWineyfeedClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            handleConsumeButton: () -> Unit,
            handleSaveButton: () -> Unit
        ): WineyUploadDialogFragment {
            return WineyUploadDialogFragment().apply {
                handleConsume = handleConsumeButton
                handleSave = handleSaveButton
            }
        }
    }
}
