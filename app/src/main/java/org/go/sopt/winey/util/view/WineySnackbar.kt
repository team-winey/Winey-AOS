package org.go.sopt.winey.util.view

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.LayoutWineySnackbarBinding
import org.go.sopt.winey.util.context.colorOf
import org.go.sopt.winey.util.context.drawableOf

class WineySnackbar(
    anchorView: View,
    private val isSuccess: Boolean,
    private val message: String
) {
    private val context = anchorView.context
    private val snackbar = Snackbar.make(anchorView, "", DURATION_WINEY_SNACKBAR)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding: LayoutWineySnackbarBinding =
        DataBindingUtil.inflate(inflater, R.layout.layout_winey_snackbar, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        setPosition()

        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(context.colorOf(android.R.color.transparent))
            addView(binding.root)
        }
    }

    private fun setPosition() {
        val snackbarLayoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
        snackbarLayoutParams.gravity = Gravity.TOP
        snackbar.view.layoutParams = snackbarLayoutParams
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
        private const val DURATION_WINEY_SNACKBAR = 1500

        @JvmStatic
        fun make(view: View, isSuccess: Boolean, message: String) =
            WineySnackbar(view, isSuccess, message)
    }
}
