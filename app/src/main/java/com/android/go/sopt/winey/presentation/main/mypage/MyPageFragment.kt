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

        initTargetModifyButtonClickListener()
        init1On1ButtonClickListener()
        initLevelHelpButtonClickListener()
        setupGetUserState()
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
        viewModel.getUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    binding.data = state.data
                    when (state.data.userLevel) {
                        common -> {
                            binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv1_progressbar)
                        }

                        knight -> {
                            binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv2_progressbar)
                        }

                        nobless -> {
                            binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv3_progressbar)
                        }

                        king -> {
                            binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv4_progressbar)
                        }
                    }
                }

                is UiState.Failure -> {

                }

                is UiState.Empty -> {

                }
            }
        }
    }

    companion object {
        private const val common = "평민"
        private const val knight = "기사"
        private const val nobless = "귀족"
        private const val king = "황제"
    }
}