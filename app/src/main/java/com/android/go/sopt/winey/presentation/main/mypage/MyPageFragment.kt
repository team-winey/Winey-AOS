package com.android.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyPageBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel by viewModels<MyPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtnEvent()
    }

    fun initBtnEvent() {
        binding.btnMypageTargetEdit.setOnClickListener {
            val bottomsheet = TargetAmountBottomSheetFragment()

            bottomsheet.show(this.childFragmentManager, bottomsheet.tag)
        }
    }
}