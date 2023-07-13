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

        initTargetModifyButtonClickListener()
        init1On1ButtonClickListener()
        initLevelHelpButtonClickListener()
    }

    private fun initTargetModifyButtonClickListener() {
        binding.btnMypageTargetModify.setOnSingleClickListener {
            val bottomSheet = TargetAmountBottomSheetFragment()

            bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
        }
    }

    private fun init1On1ButtonClickListener() {
        binding.btnMypage1on1.setOnClickListener {
            val dialog = ImpossibleDialogFragment()

            dialog.show(this.childFragmentManager, dialog.tag)
        }
    }

    private fun initLevelHelpButtonClickListener() {
        binding.btnMypageLevelHelp.setOnClickListener {
            val intent = Intent(context, MypageHelpActivity::class.java)

            startActivity(intent)
        }
    }
}