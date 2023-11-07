package org.go.sopt.winey.util.fragment

import android.os.Bundle
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentAlertDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class WineyDialogFragment :
    BindingDialogFragment<FragmentAlertDialogBinding>(R.layout.fragment_alert_dialog) {
    private var handleNegative: (() -> Unit)? = null
    private var handlePositive: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)

        val title = arguments?.getString(ARG_TITLE)
        val negativeButtonLabel = arguments?.getString(ARG_NEGATIVE_BUTTON_LABEL)
        val subTitle = arguments?.getString(ARG_SUB_TITLE)
        val positiveButtonLabel = arguments?.getString(ARG_POSITIVE_BUTTON_LABEL)

        if (title != null && subTitle != null && negativeButtonLabel != null && positiveButtonLabel != null) {
            initDialogText(title, subTitle, positiveButtonLabel, negativeButtonLabel)
        }
        initNegativeButtonClickListener(handleNegative)
        initPositiveButtonClickListener(handlePositive)
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

    private fun initNegativeButtonClickListener(handleNegativeButton: (() -> Unit)?) {
        binding.btnDialogNegative.setOnClickListener {
            handleNegativeButton?.invoke()
            dismiss()
        }
    }

    private fun initPositiveButtonClickListener(handlePositiveButton: (() -> Unit)?) {
        binding.btnDialogPositive.setOnClickListener {
            handlePositiveButton?.invoke()
            dismiss()
        }
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_SUB_TITLE = "subTitle"
        private const val ARG_NEGATIVE_BUTTON_LABEL = "negativeButtonLabel"
        private const val ARG_POSITIVE_BUTTON_LABEL = "positiveButtonLabel"

        fun newInstance(
            title: String,
            subTitle: String,
            negativeButtonLabel: String,
            positiveButtonLabel: String,
            handleNegativeButton: () -> Unit,
            handlePositiveButton: () -> Unit
        ): WineyDialogFragment {
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_SUB_TITLE, subTitle)
            args.putString(ARG_NEGATIVE_BUTTON_LABEL, negativeButtonLabel)
            args.putString(ARG_POSITIVE_BUTTON_LABEL, positiveButtonLabel)

            val fragment = WineyDialogFragment()
            fragment.arguments = args

            fragment.handleNegative = handleNegativeButton
            fragment.handlePositive = handlePositiveButton

            return fragment
        }
    }
}
