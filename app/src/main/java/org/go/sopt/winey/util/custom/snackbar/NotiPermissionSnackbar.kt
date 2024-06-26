package org.go.sopt.winey.util.custom.snackbar

import android.graphics.Paint
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.transition.SlideDistanceProvider.GravityFlag
import org.go.sopt.winey.util.context.stringOf

class NotiPermissionSnackbar(
    anchorView: View,
    @GravityFlag gravity: Int,
    message: String
) : WineySnackbar(anchorView, gravity, message) {
    override fun initResultIcon(isSuccess: Boolean) {
        binding.ivSnackbarResult.isGone = true
    }

    override fun setAction(@StringRes resId: Int, onClicked: () -> Unit) {
        binding.tvSnackbarAction.apply {
            isVisible = true
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            text = context.stringOf(resId)
            setOnClickListener {
                onClicked.invoke()
            }
        }
    }
}
