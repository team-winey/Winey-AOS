package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentAmountBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo: 에디트텍스트에 포커스가 놓이면 레이아웃 테두리 색상 변경
    }
}