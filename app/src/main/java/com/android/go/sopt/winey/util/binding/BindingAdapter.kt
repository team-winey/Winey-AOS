package com.android.go.sopt.winey.util.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.util.code.NicknameErrorCode.*
import com.android.go.sopt.winey.util.context.colorOf
import com.android.go.sopt.winey.util.context.drawableOf
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.view.InputUiState
import com.android.go.sopt.winey.util.view.InputUiState.*
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
        placeholder(R.drawable.img_wineyfeed_default)
        transformations(RoundedCornersTransformation(10F))
    }
}

@BindingAdapter("setImageUriWithCoil", "setDefaultDrawable")
fun ImageView.setRoundedImage(imageUri: Uri?, drawable: Drawable) {
    if (imageUri == null) {
        setImageDrawable(drawable)
        return
    }

    scaleType = ScaleType.CENTER_CROP
    load(imageUri) {
        transformations(RoundedCornersTransformation(10f))
    }
}

@BindingAdapter("setBackground")
fun EditText.setBackground(inputUiState: InputUiState) {
    background = when (inputUiState) {
        is Empty -> context.drawableOf(R.drawable.sel_nickname_edittext_focus_color)
        is Success -> context.drawableOf(R.drawable.shape_blue_line_5_rect)
        is Failure -> context.drawableOf(R.drawable.shape_red_line_5_rect)
    }
}

@BindingAdapter("setHelperText")
fun TextView.setHelperText(inputUiState: InputUiState) {
    when (inputUiState) {
        is Empty -> {
            visibility = View.INVISIBLE
        }

        is Success -> {
            visibility = View.VISIBLE
            text = context.stringOf(R.string.nickname_valid)
        }

        is Failure -> {
            visibility = View.VISIBLE
            text = when (inputUiState.code) {
                CODE_INVALID_LENGTH -> context.stringOf(R.string.nickname_invalid_length_error)
                CODE_SPACE_SPECIAL_CHAR -> context.stringOf(R.string.nickname_space_special_char_error)
                CODE_UNCHECKED_DUPLICATION -> context.stringOf(R.string.nickname_unchecked_duplication_error)
                CODE_DUPLICATE -> context.stringOf(R.string.nickname_duplicate_error)
            }
        }
    }
}

@BindingAdapter("setHelperTextColor")
fun TextView.setHelperTextColor(inputUiState: InputUiState) {
    when (inputUiState) {
        is Empty -> setTextColor(context.colorOf(R.color.gray_200))
        is Success -> setTextColor(context.colorOf(R.color.blue_500))
        is Failure -> setTextColor(context.colorOf(R.color.red_500))
    }
}

@BindingAdapter("setFeedLikeImage")
fun setLikeImage(view: ImageView, isLiked: Boolean) {
    val imageRes = if (isLiked) {
        R.drawable.ic_wineyfeed_liked
    } else {
        R.drawable.ic_wineyfeed_disliked
    }
    view.setImageResource(imageRes)
}
