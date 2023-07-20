package com.android.go.sopt.winey.util.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.android.go.sopt.winey.R
import java.text.DecimalFormat

@BindingAdapter("likedAmount")
fun applyNumberFormat(view: TextView, amount: Long) {
    val decimalFormat = DecimalFormat("#,###")
    val formattedValue = when {
        amount >= 1E9 -> "${(amount.toFloat() / 1E9).toInt()}B"
        amount >= 1E6 -> "${(amount.toFloat() / 1E6).toInt()}M"
        amount >= 1E3 -> "${(amount.toFloat() / 1E3).toInt()}K"
        else -> decimalFormat.format(amount)
    }
    view.text = formattedValue
}

@BindingAdapter("setAmount", "setPrefix", "setSuffix", requireAll = false)
fun TextView.setFormattedNumber(amount: Long, prefix: String?, suffix: String?) {
    val pre = prefix ?: ""
    val suf = suffix ?: ""

    val decimalFormat = DecimalFormat("#,###")
    val formattedNumber = decimalFormat.format(amount)
    text = "$pre$formattedNumber$suf"
}

@BindingAdapter("imageUrl")
fun loadImager(view: ImageView, imageurl: String) {
    view.load(imageurl) {
        placeholder(R.drawable.img_feed_default)
        transformations(RoundedCornersTransformation(10F))
    }
}

@BindingAdapter("setImageUriWithCoil", "setDefaultDrawable")
fun ImageView.setRoundedImage(imageUri: Uri?, drawable: Drawable) {
    if (imageUri == null) {
        this.setImageDrawable(drawable)
        return
    }

    scaleType = ScaleType.CENTER_CROP
    load(imageUri) {
        transformations(RoundedCornersTransformation(10f))
    }
}


