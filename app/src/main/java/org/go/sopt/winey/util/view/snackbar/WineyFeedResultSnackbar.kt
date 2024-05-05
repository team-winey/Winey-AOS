package org.go.sopt.winey.util.view.snackbar

import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.transition.SlideDistanceProvider.GravityFlag
import org.go.sopt.winey.R
import org.go.sopt.winey.util.context.drawableOf

class WineyFeedResultSnackbar(
    anchorView: View,
    @GravityFlag gravity: Int,
    message: String
) : WineySnackbar(anchorView, gravity, message) {
    private val context = anchorView.context

    override fun initResultIcon(isSuccess: Boolean) {
        if (isSuccess) {
            binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_success))
        } else {
            binding.ivSnackbarResult.setImageDrawable(context.drawableOf(R.drawable.ic_snackbar_fail))
        }
    }

    override fun setAction(resId: Int, onClicked: () -> Unit) {
        binding.tvSnackbarAction.isVisible = false
    }
}
