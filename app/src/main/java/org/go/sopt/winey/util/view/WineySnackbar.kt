package org.go.sopt.winey.util.view

import android.graphics.Paint
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
    private val message: String,
    private val isSuccess: Boolean,
    private val isNotiType: Boolean = false
) {
    private val context = anchorView.context
    private val snackbar = Snackbar.make(anchorView, "", DURATION_WINEY_SNACKBAR)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding: LayoutWineySnackbarBinding =
        DataBindingUtil.inflate(inflater, R.layout.layout_winey_snackbar, null, false)

    init {
        initView()
    }

    private fun initView() {
        setPosition()
        initLayout()

        initSuccessIcon()
        initMessage()
        initActionText()
    }

    private fun setPosition() {
        val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackbar.view.layoutParams = layoutParams
    }

    private fun initLayout() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(context.colorOf(android.R.color.transparent))
            addView(binding.root)
        }
    }

    private fun initSuccessIcon() {
        if (isSuccess) {
            binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_success))
        } else {
            binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_fail))
        }
    }

    private fun initMessage() {
        binding.tvSnackbarMsg.text = message
    }

    private fun initActionText() {
        if (isNotiType) {
            binding.tvSnackbarAction.visibility = View.VISIBLE
            binding.tvSnackbarAction.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        } else {
            binding.tvSnackbarAction.visibility = View.INVISIBLE
        }
    }

    fun show() {
        snackbar.show()
    }

    companion object {
        private const val DURATION_WINEY_SNACKBAR = 1500

        @JvmStatic
        fun make(view: View, message: String, isSuccess: Boolean, isNotiType: Boolean) =
            WineySnackbar(view, message, isSuccess, isNotiType)
    }
}
