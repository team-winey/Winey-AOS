package com.go.sopt.winey.util.fragment

import android.os.Bundle
import android.view.View
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.FragmentAlertDialogBinding
import com.go.sopt.winey.util.binding.BindingDialogFragment

class WineyDialogFragment(
    private val title: String,
    private val subTitle: String,
    private val negativeButtonLabel: String,
    private val positiveButtonLabel: String,
    val handleNegativeButton: () -> Unit,
    val handlePositiveButton: () -> Unit
) : BindingDialogFragment<FragmentAlertDialogBinding>(R.layout.fragment_alert_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        initDialogText(title, subTitle, positiveButtonLabel, negativeButtonLabel)
        initNegativeButtonClickListener(handleNegativeButton)
        initPositiveButtonClickListener(handlePositiveButton)
    }

    private fun initDialogText(
        title: String,
        subTitle: String,
        positiveButtonLabel: String,
        negativeButtonLabel: String
    ) {
        binding.tvDialogTitle.text = title
        binding.tvDialogSub.text = subTitle
        binding.btnDialogPositive.text = positiveButtonLabel
        binding.btnDialogNegative.text = negativeButtonLabel
    }

    private fun initNegativeButtonClickListener(handleNegativeButton: () -> Unit) {
        binding.btnDialogNegative.setOnClickListener {
            handleNegativeButton.invoke()
            dismiss()
        }
    }

    private fun initPositiveButtonClickListener(handlePositiveButton: () -> Unit) {
        binding.btnDialogPositive.setOnClickListener {
            handlePositiveButton.invoke()
            dismiss()
        }
    }
}
