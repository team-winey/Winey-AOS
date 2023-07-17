package com.android.go.sopt.winey.presentation.main.mypage

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentTargetAmountBottomSheetBinding
import com.android.go.sopt.winey.util.binding.BindingBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior


class TargetAmountBottomSheetFragment :
    BindingBottomSheetDialogFragment<FragmentTargetAmountBottomSheetBinding>(R.layout.fragment_target_amount_bottom_sheet) {
    private val viewModel by viewModels<MyPageViewModel>()

    override fun onStart() {
        super.onStart()

        initScreenHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCancelButtonClickListener()
    }


    fun initCancelButtonClickListener() {
        binding.btnTargetAmountCancel.setOnClickListener {
            this.dismiss()
        }
    }

    fun initScreenHeight() {
        if (dialog != null) {
            val bottomSheet: View =
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        val view = view
        view?.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view.measuredHeight
            val maxHeight = resources.displayMetrics.heightPixels * 3 / 4
            bottomSheetBehavior.maxHeight = maxHeight
            parent.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}