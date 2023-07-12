package com.android.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentImpossibleDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class ImpossibleDialogFragment :
    BindingDialogFragment<FragmentImpossibleDialogBinding>(R.layout.fragment_impossible_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtnEvent()
    }

    fun initBtnEvent() {
        binding.btnImpossibleDialogCancel.setOnClickListener {
            this.dismiss()
        }
    }
}