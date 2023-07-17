package com.android.go.sopt.winey.util.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
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

@BindingAdapter("setAmount","setPrefix","setSuffix", requireAll = false)
fun TextView.setFormattedNumber(amount: Long, prefix: String?, suffix: String?) {
    val pre = prefix ?: ""
    val suf = suffix ?: ""

    val decimalFormat = DecimalFormat("#,###")
    val formattedNumber = decimalFormat.format(amount)
    text = "$pre$formattedNumber$suf"
}

@BindingAdapter("imageUrl")
fun loadImager(view: ImageView, imageurl: String) {
    view.load(imageurl)
}