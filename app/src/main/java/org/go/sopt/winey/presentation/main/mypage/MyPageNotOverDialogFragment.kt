package org.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentMypageNotOverDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class MyPageNotOverDialogFragment :
    BindingDialogFragment<FragmentMypageNotOverDialogBinding>(R.layout.fragment_mypage_not_over_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCancelButtonClickListener()
    }

    private fun initCancelButtonClickListener() {
        binding.btnImpossibleDialogCancel.setOnClickListener {
            this.dismiss()
        }
    }
}
