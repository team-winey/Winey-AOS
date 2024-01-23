package org.go.sopt.winey.util.view

import android.graphics.Paint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.LayoutWineySnackbarBinding
import org.go.sopt.winey.util.context.colorOf
import org.go.sopt.winey.util.context.drawableOf
import org.go.sopt.winey.util.context.stringOf

class WineySnackbar(
    anchorView: View,
    private val message: String,
    private val type: SnackbarType
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
        // 스낵바 공통 부분
        setPosition()
        initLayout()
        initMessage()

        // 타입에 따라 뷰 초기화 다르게
        initResultIcon()
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

    private fun initMessage() {
        binding.tvSnackbarMsg.text = message
    }

    private fun initResultIcon() {
        when (type) {
            is SnackbarType.WineyFeedResult -> {
                if (type.isSuccess) {
                    binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_success))
                } else {
                    binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_fail))
                }
            }

            is SnackbarType.NotiPermission -> {
                binding.ivSnackbarResult.visibility = View.GONE
            }
        }
    }

    private fun initActionText() {
        when (type) {
            is SnackbarType.WineyFeedResult -> {
                binding.tvSnackbarAction.visibility = View.INVISIBLE
            }

            is SnackbarType.NotiPermission -> {
                binding.tvSnackbarAction.apply {
                    visibility = View.VISIBLE
                    paintFlags = Paint.UNDERLINE_TEXT_FLAG
                }
            }
        }
    }

    fun setAction(@StringRes resId: Int, onClicked: () -> Unit) {
        binding.tvSnackbarAction.apply {
            text = context.stringOf(resId)
            setOnClickListener {
                onClicked.invoke()
            }
        }
    }

    fun show() {
        snackbar.show()
    }

    companion object {
        private const val DURATION_WINEY_SNACKBAR = 2000

        @JvmStatic
        fun make(view: View, message: String, type: SnackbarType) =
            WineySnackbar(
                anchorView = view,
                message = message,
                type = type
            )
    }
}
