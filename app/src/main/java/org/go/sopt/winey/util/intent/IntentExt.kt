package org.go.sopt.winey.util.intent

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Parcelable

inline fun <reified T : Parcelable> Intent.getCompatibleParcelableExtra(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> getParcelableExtra(key) as? T
}
