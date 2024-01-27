package org.go.sopt.winey.util.view.snackbar

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.SlideDistanceProvider.GravityFlag
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.LayoutWineySnackbarBinding
import org.go.sopt.winey.util.context.colorOf

abstract class WineySnackbar(
    anchorView: View,
    @GravityFlag
    private val gravity: Int,
    private val message: String
) {
    private val snackbar = Snackbar.make(anchorView, "", DURATION_WINEY_SNACKBAR)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    private val inflater = LayoutInflater.from(anchorView.context)
    protected val binding: LayoutWineySnackbarBinding =
        DataBindingUtil.inflate(inflater, R.layout.layout_winey_snackbar, null, false)

    init {
        initView()
    }

    private fun initView() {
        initLayout()
        setPosition()
        initMessage()
    }

    private fun initLayout() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(context.colorOf(android.R.color.transparent))
            addView(binding.root)
        }
    }

    private fun setPosition() {
        val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = gravity
        snackbar.view.layoutParams = layoutParams
    }

    private fun initMessage() {
        binding.tvSnackbarMsg.text = message
    }

    fun show() {
        snackbar.show()
    }

    abstract fun initResultIcon(isSuccess: Boolean)

    abstract fun setAction(@StringRes resId: Int, onClicked: () -> Unit)

    companion object {
        private const val DURATION_WINEY_SNACKBAR = 2000
    }
}
