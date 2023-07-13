package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.DialogMyfeedAlertDeleteBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment

class MyFeedDialogFragment :
    BindingDialogFragment<DialogMyfeedAlertDeleteBinding>(R.layout.dialog_myfeed_alert_delete) {

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
        binding.btnDialogDelete.setOnClickListener {
            /* 마이위니 구현완료되면 인터랙션 구현 */
        }
    }
}