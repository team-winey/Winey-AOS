package org.go.sopt.winey.util.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentAlertDialogBinding
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.binding.BindingDialogFragment
import org.go.sopt.winey.util.intent.getCompatibleParcelable

class WineyDialogFragment :
    BindingDialogFragment<FragmentAlertDialogBinding>(R.layout.fragment_alert_dialog) {
    private val dialogLabel: WineyDialogLabel? by lazy { arguments?.getCompatibleParcelable(KEY_DIALOG_LABEL) }
    private var handleNegative: () -> Unit = {}
    private var handlePositive: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        binding.data = dialogLabel

        initButtonStyle()
        initNegativeButtonClickListener(handleNegative)
        initPositiveButtonClickListener(handlePositive)
    }

    private fun initButtonStyle() {
        dialogLabel?.let {
            if (it.negativeButtonLabel == null) {
                removeNegativeButton()
            }
        }
    }

    private fun removeNegativeButton() {
        binding.btnDialogNegative.isGone = true

        val positiveButton = binding.btnDialogPositive
        val params = positiveButton.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        positiveButton.layoutParams = params
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

    companion object {
        private const val KEY_DIALOG_LABEL = "winey_dialog_label"

        fun newInstance(
            wineyDialogLabel: WineyDialogLabel,
            handleNegativeButton: () -> Unit,
            handlePositiveButton: () -> Unit
        ): WineyDialogFragment {
            return WineyDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DIALOG_LABEL, wineyDialogLabel)
                }
                handleNegative = handleNegativeButton
                handlePositive = handlePositiveButton
            }
        }
    }
}
