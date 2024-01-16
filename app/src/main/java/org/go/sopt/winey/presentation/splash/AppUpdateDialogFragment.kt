package org.go.sopt.winey.presentation.splash

import android.os.Bundle
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentAppUpdateDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class AppUpdateDialogFragment :
    BindingDialogFragment<FragmentAppUpdateDialogBinding>(R.layout.fragment_app_update_dialog) {
    private var onUpdateButtonClicked: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppUpdateButtonClickListener()
    }

    private fun initAppUpdateButtonClickListener() {
        binding.btnUpdate.setOnClickListener {
            onUpdateButtonClicked.invoke()
            dismiss()
        }
    }

    companion object {
        fun newInstance(onUpdateButtonClicked: () -> Unit) =
            AppUpdateDialogFragment().apply {
                this.onUpdateButtonClicked = onUpdateButtonClicked
            }
    }
}
