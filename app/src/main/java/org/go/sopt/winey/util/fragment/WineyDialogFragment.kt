package org.go.sopt.winey.util.fragment

import android.os.Bundle
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentAlertDialogBinding
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.binding.BindingDialogFragment
import org.go.sopt.winey.util.intent.getCompatibleParcelableExtra

class WineyDialogFragment :
    BindingDialogFragment<FragmentAlertDialogBinding>(R.layout.fragment_alert_dialog) {
    private var handleNegative: (() -> Unit) = {}
    private var handlePositive: (() -> Unit) = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)

        binding.data = arguments?.getCompatibleParcelableExtra(ARG)

        initNegativeButtonClickListener(handleNegative)
        initPositiveButtonClickListener(handlePositive)
    }

    private fun initNegativeButtonClickListener(handleNegativeButton: (() -> Unit)) {
        binding.btnDialogNegative.setOnClickListener {
            handleNegativeButton.invoke()
            dismiss()
        }
    }

    private fun initPositiveButtonClickListener(handlePositiveButton: (() -> Unit)) {
        binding.btnDialogPositive.setOnClickListener {
            handlePositiveButton.invoke()
            dismiss()
        }
    }

    companion object {
        private const val ARG = "arguments"
        fun newInstance(
            wineyDialogLabel: WineyDialogLabel,
            handleNegativeButton: () -> Unit,
            handlePositiveButton: () -> Unit
        ): WineyDialogFragment {
            val args = Bundle().apply {
                putParcelable(ARG, wineyDialogLabel)
            }

            return WineyDialogFragment().apply {
                arguments = args
                handleNegative = handleNegativeButton
                handlePositive = handlePositiveButton
            }
        }
    }
}
