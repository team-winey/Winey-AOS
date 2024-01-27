package org.go.sopt.winey.util.context

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.go.sopt.winey.R
import org.go.sopt.winey.util.view.snackbar.NotiPermissionSnackbar
import org.go.sopt.winey.util.view.snackbar.SnackbarType
import org.go.sopt.winey.util.view.snackbar.WineyFeedResultSnackbar
import org.go.sopt.winey.util.view.snackbar.WineySnackbar

/** Hide keyboard from window */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.snackBar(anchorView: View, message: () -> String) {
    Snackbar.make(anchorView, message(), Snackbar.LENGTH_SHORT).show()
}

fun Context.wineySnackbar(
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

fun Context.stringOf(@StringRes resId: Int) = getString(resId)

fun Context.colorOf(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)

fun Context.drawableOf(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)
