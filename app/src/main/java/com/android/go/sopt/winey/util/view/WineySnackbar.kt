package com.android.go.sopt.winey.util.view

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.LayoutWineySnackbarBinding
import com.android.go.sopt.winey.util.context.colorOf
import com.android.go.sopt.winey.util.context.drawableOf
import com.google.android.material.snackbar.Snackbar

class WineySnackbar(
    view: View,
    private val isSuccess: Boolean,
    private val message: String
) {
    private val context = view.context
    private val snackbar = Snackbar.make(view, "", DURATION_WINEY_SNACKBAR)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding: LayoutWineySnackbarBinding =
        DataBindingUtil.inflate(inflater, R.layout.layout_winey_snackbar, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(context.colorOf(android.R.color.transparent))
            addView(binding.root, 0)
        }
    }

    private fun initData() {
        if (isSuccess) {
            binding.ivSnackbar.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_success))
        } else {
            binding.ivSnackbar.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_fail))
        }
        binding.tvSnackbar.text = message
    }

    fun show() {
        snackbar.show()
    }

    companion object {
        private const val DURATION_WINEY_SNACKBAR = 4000

        @JvmStatic
        fun make(view: View, isSuccess: Boolean, message: String) =
            WineySnackbar(view, isSuccess, message)
    }
}
