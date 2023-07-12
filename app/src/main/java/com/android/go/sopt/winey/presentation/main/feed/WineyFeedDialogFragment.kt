package com.android.go.sopt.winey.presentation.main.feed

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.DialogWineyfeedAlertNoGoalBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedDialogFragment :
    BindingDialogFragment<DialogWineyfeedAlertNoGoalBinding>(R.layout.dialog_wineyfeed_alert_no_goal) {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFabClickListener()
    }

    private fun initFabClickListener() {
        binding.btnDialogCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnDialogSetgoal.setOnClickListener {
            /* 목표 구현완료되면 인터랙션 구현 */
        }
    }
}