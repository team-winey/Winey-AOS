package org.go.sopt.winey.util.fragment

import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.go.sopt.winey.R
import org.go.sopt.winey.util.view.snackbar.NotiPermissionSnackbar
import org.go.sopt.winey.util.view.snackbar.SnackbarType
import org.go.sopt.winey.util.view.snackbar.WineyFeedResultSnackbar
import org.go.sopt.winey.util.view.snackbar.WineySnackbar

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.snackBar(anchorView: View, message: () -> String) {
    Snackbar.make(anchorView, message(), Snackbar.LENGTH_SHORT).show()
}

fun Fragment.wineySnackbar(
    anchorView: View,
    message: String,
    type: SnackbarType
) {
    when (type) {
        is SnackbarType.WineyFeedResult -> {
            WineyFeedResultSnackbar(
                anchorView = anchorView,
                gravity = Gravity.TOP,
                message = message
            ).apply {
                initResultIcon(isSuccess = type.isSuccess)
                show()
            }
        }

        is SnackbarType.NotiPermission -> {
            NotiPermissionSnackbar(
                anchorView = anchorView,
                gravity = Gravity.TOP,
                message = message
            ).apply {
                setAction(
                    resId = R.string.snackbar_noti_permission_setting_text,
                    onClicked = type.onActionClicked
                )
                show()
            }
        }
    }
}

fun Fragment.stringOf(@StringRes resId: Int) = getString(resId)

fun Fragment.colorOf(@ColorRes resId: Int) = ContextCompat.getColor(requireContext(), resId)

fun Fragment.drawableOf(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(requireContext(), resId)

val Fragment.viewLifeCycle
    get() = viewLifecycleOwner.lifecycle

val Fragment.viewLifeCycleScope
    get() = viewLifecycleOwner.lifecycleScope
