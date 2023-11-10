package org.go.sopt.winey.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WineyDialogLabel(
    val title: String,
    val subTitle: String,
    val negativeButtonLabel: String,
    val positiveButtonLabel: String
) : Parcelable
