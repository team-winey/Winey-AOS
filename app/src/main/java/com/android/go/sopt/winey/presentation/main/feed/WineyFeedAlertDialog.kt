package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.DialogWineyfeedAlertNoGoalBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedAlertDialog : BindingDialogFragment<DialogWineyfeedAlertNoGoalBinding>(R.layout.dialog_wineyfeed_alert_no_goal) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setNavigateTo() {
        binding.btnDialogCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnDialogSetgoal.setOnClickListener {
            /* 목표 구현완료되면 인터랙션 구현 */
        }
    }
}