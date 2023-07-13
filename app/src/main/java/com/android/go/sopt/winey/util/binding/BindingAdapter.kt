package com.android.go.sopt.winey.util.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DecimalFormat

@BindingAdapter("moneyAmount")
fun applyNumberFormatToMoney(view: TextView, amount: Long) {
    val decimalFormat = DecimalFormat("#,###")
    view.text = "${decimalFormat.format(amount)}원 절약"
}

@BindingAdapter("likedAmount")
fun applyNumberFormat(view: TextView, amount: Long) {
    val decimalFormat = DecimalFormat("#,###")
    view.text = decimalFormat.format(amount)
}
