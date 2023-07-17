package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyfeedNoGoalDialogBinding
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedDialogFragment :
    BindingDialogFragment<FragmentWineyfeedNoGoalDialogBinding>(R.layout.fragment_wineyfeed_no_goal_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnClickListener()
    }

    private fun initBtnClickListener() {
        binding.btnDialogCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnDialogSetgoal.setOnClickListener {
            this.dismiss()
            navigateToMyPage()
        }
    }

    private fun navigateToMyPage() {
        parentFragmentManager.commit {
            replace(R.id.fcv_main, MyPageFragment())
            addToBackStack(null)
        }
    }
}