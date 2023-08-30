package com.go.sopt.winey.util.activity

import android.app.Activity
import android.view.View
import com.go.sopt.winey.util.context.hideKeyboard

/** Hide keyboard from activity window */
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
