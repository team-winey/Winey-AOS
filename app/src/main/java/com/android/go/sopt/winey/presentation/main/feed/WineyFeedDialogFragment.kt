package com.android.go.sopt.winey.presentation.main.feed

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyfeedNoGoalDialogBinding
import com.android.go.sopt.winey.presentation.main.mypage.TargetAmountBottomSheetFragment
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedDialogFragment :
    BindingDialogFragment<FragmentWineyfeedNoGoalDialogBinding>(R.layout.fragment_wineyfeed_no_goal_dialog) {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnlickListener()
    }

    private fun initBtnlickListener() {
        binding.btnDialogCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnDialogSetgoal.setOnClickListener {
            val bottomSheet = TargetAmountBottomSheetFragment()
            bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
        }
    }
}