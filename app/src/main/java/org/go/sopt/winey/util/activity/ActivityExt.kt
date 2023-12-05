package org.go.sopt.winey.util.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import org.go.sopt.winey.util.context.hideKeyboard

/** Hide keyboard from activity window */
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showReportGoogleForm() {
    val formUrl = "https://docs.google.com/forms/d/1fymNx8ALanWWzwR4O2s8hpt76mnRClOmfDx4Vbdk2kk"
    Intent(Intent.ACTION_VIEW, Uri.parse(formUrl)).apply {
        startActivity(this)
    }
}
