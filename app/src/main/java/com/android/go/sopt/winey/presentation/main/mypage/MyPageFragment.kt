package com.android.go.sopt.winey.presentation.main.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyPageBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel by viewModels<MyPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel

        initTargetModifyButtonClickListener()
        init1On1ButtonClickListener()
        initLevelHelpButtonClickListener()
        viewModel.getUser()
        //setupGetUserState()
    }

    private fun initTargetModifyButtonClickListener() {
        binding.btnMypageTargetModify.setOnSingleClickListener {
            val bottomSheet = TargetAmountBottomSheetFragment()

            bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
        }
    }

    private fun init1On1ButtonClickListener() {
        binding.btnMypage1on1.setOnClickListener {
            val dialog = MyPageDialogFragment()

            dialog.show(this.childFragmentManager, dialog.tag)
        }
    }

    private fun initLevelHelpButtonClickListener() {
        binding.btnMypageLevelHelp.setOnClickListener {
            val intent = Intent(context, MypageHelpActivity::class.java)

            startActivity(intent)
        }
    }

    private fun setupGetUserState() {
        viewModel.getUserState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {

                }

                is UiState.Failure -> {

                }

                is UiState.Empty -> {

                }
            }
        }
    }
}