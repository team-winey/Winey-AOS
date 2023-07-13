package com.android.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMypageNotOverDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class MyPageDialogFragment :
    BindingDialogFragment<FragmentMypageNotOverDialogBinding>(R.layout.fragment_mypage_not_over_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCancelButtonClickListener()
    }

    fun initCancelButtonClickListener() {
        binding.btnImpossibleDialogCancel.setOnClickListener {
            this.dismiss()
        }
    }
}