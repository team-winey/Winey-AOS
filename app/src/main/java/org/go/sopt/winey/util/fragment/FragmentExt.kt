package org.go.sopt.winey.util.fragment

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
import org.go.sopt.winey.util.view.SnackbarType
import org.go.sopt.winey.util.view.WineySnackbar

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
    val snackbar = WineySnackbar.make(anchorView, message, type)
    if (type is SnackbarType.NotiPermission) {
        snackbar.setAction(
            resId = R.string.snackbar_noti_permission_setting_text,
            onClicked = type.onActionClicked
        )
    }
    snackbar.show()
}

fun Fragment.stringOf(@StringRes resId: Int) = getString(resId)

fun Fragment.colorOf(@ColorRes resId: Int) = ContextCompat.getColor(requireContext(), resId)

fun Fragment.drawableOf(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(requireContext(), resId)

val Fragment.viewLifeCycle
    get() = viewLifecycleOwner.lifecycle

val Fragment.viewLifeCycleScope
    get() = viewLifecycleOwner.lifecycleScope
