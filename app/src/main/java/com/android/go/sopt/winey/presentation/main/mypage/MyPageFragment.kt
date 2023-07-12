package com.android.go.sopt.winey.presentation.main.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyPageBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel by viewModels<MyPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtnEvent()
    }

    fun initBtnEvent() {
        binding.btnMypageTargetEdit.setOnSingleClickListener {
            val bottomsheet = TargetAmountBottomSheetFragment()

            bottomsheet.show(this.childFragmentManager, bottomsheet.tag)
        }

        binding.btnMypage1on1.setOnClickListener {
            val dialog = ImpossibleDialogFragment()

            dialog.show(this.childFragmentManager, dialog.tag)
        }

        binding.btnMypageLevelHelp.setOnClickListener {
            val intent = Intent(context, MypageHelpActivity::class.java)

            startActivity(intent)
        }
    }
}