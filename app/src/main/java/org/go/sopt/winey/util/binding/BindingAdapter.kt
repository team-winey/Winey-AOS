package org.go.sopt.winey.util.binding

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import de.hdodenhof.circleimageview.CircleImageView
import org.go.sopt.winey.R
import org.go.sopt.winey.presentation.nickname.NicknameActivity.Companion.VAL_MY_PAGE_SCREEN
import org.go.sopt.winey.presentation.nickname.NicknameActivity.Companion.VAL_STORY_SCREEN
import org.go.sopt.winey.util.code.ErrorCode.*
import org.go.sopt.winey.util.context.colorOf
import org.go.sopt.winey.util.context.drawableOf
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.view.InputUiState
import org.go.sopt.winey.util.view.InputUiState.*
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
    text = context.getString(R.string.mypage_formatted_number, pre, formattedNumber, suf)
}

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(imageUrl: String?) {
    if (imageUrl == null) return
    load(imageUrl) {
        placeholder(R.drawable.img_wineyfeed_default)
        transformations(RoundedCornersTransformation(10F))
    }
}

@BindingAdapter("setUploadImageUri")
fun ImageView.setUploadImageUri(imageUri: Uri?) {
    if (imageUri == null) {
        visibility = View.INVISIBLE
        return
    }

    visibility = View.VISIBLE
    load(imageUri) {
        placeholder(R.drawable.img_wineyfeed_default)
        transformations(RoundedCornersTransformation(10F))
    }
}

@BindingAdapter("setUploadContentBackground")
fun EditText.setUploadContentBackground(inputUiState: InputUiState) {
    background = if (inputUiState is Empty || inputUiState is Success) {
        context.drawableOf(R.drawable.sel_upload_edittext_focus_color)
    } else {
        context.drawableOf(R.drawable.shape_red_line_5_rect)
    }
}

@BindingAdapter("setUploadContentHelperText")
fun TextView.setUploadContentHelperText(inputUiState: InputUiState) {
    if (inputUiState is Empty || inputUiState is Success) {
        visibility = View.INVISIBLE
        return
    }

    if (inputUiState is Failure) {
        visibility = View.VISIBLE
        if (inputUiState.code == CODE_INVALID_LENGTH) {
            text = context.stringOf(R.string.upload_content_error_text)
        }
    }
}

@BindingAdapter("setNicknameBackground")
fun EditText.setNicknameBackground(inputUiState: InputUiState) {
    background = when (inputUiState) {
        is Empty -> context.drawableOf(R.drawable.sel_nickname_edittext_focus_color)
        is Success -> context.drawableOf(R.drawable.shape_blue_line_5_rect)
        is Failure -> context.drawableOf(R.drawable.shape_red_line_5_rect)
    }
}

@BindingAdapter("setNicknameHelperText")
fun TextView.setNicknameHelperText(inputUiState: InputUiState) {
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
                CODE_BLANK_INPUT -> context.stringOf(R.string.nickname_blank_input_error)
                CODE_INVALID_LENGTH -> context.stringOf(R.string.nickname_invalid_length_error)
                CODE_SPACE_SPECIAL_CHAR -> context.stringOf(R.string.nickname_space_special_char_error)
                CODE_UNCHECKED_DUPLICATION -> context.stringOf(R.string.nickname_unchecked_duplication_error)
                CODE_DUPLICATED -> context.stringOf(R.string.nickname_duplicated_error)
            }
        }
    }
}

@BindingAdapter("setNicknameHelperTextColor")
fun TextView.setNicknameHelperTextColor(inputUiState: InputUiState) {
    when (inputUiState) {
        is Empty -> setTextColor(context.colorOf(R.color.gray_200))
        is Success -> setTextColor(context.colorOf(R.color.blue_500))
        is Failure -> setTextColor(context.colorOf(R.color.red_500))
    }
}

@BindingAdapter(
    "prevScreenName",
    "inputNicknameLength",
    "originalNicknameLength",
    requireAll = false
)
fun TextView.setNicknameCounter(
    prevScreenName: String,
    inputNicknameLength: Int,
    originalNicknameLength: Int
) {
    when (prevScreenName) {
        VAL_STORY_SCREEN -> {
            text = context.getString(R.string.nickname_counter, inputNicknameLength)
        }

        VAL_MY_PAGE_SCREEN -> {
            text = if (inputNicknameLength == 0) {
                // 입력 값이 비어있을 때는 원래 닉네임의 글자 수 표시
                context.getString(R.string.nickname_counter, originalNicknameLength)
            } else {
                context.getString(R.string.nickname_counter, inputNicknameLength)
            }
        }
    }
}

@BindingAdapter("switchCloseButtonVisibility")
fun ImageView.switchCloseButtonVisibility(prevScreenName: String) {
    when (prevScreenName) {
        VAL_STORY_SCREEN -> visibility = View.GONE
        VAL_MY_PAGE_SCREEN -> visibility = View.VISIBLE
    }
}

@BindingAdapter("switchTitleText")
fun TextView.switchTitleText(prevScreenName: String) {
    when (prevScreenName) {
        VAL_STORY_SCREEN ->
            text =
                context.stringOf(R.string.nickname_default_title)

        VAL_MY_PAGE_SCREEN ->
            text =
                context.stringOf(R.string.nickname_mypage_title)
    }
}

@BindingAdapter("switchCompleteButtonText")
fun TextView.switchCompleteButtonText(prevScreenName: String) {
    when (prevScreenName) {
        VAL_STORY_SCREEN ->
            text =
                context.stringOf(R.string.nickname_start_btn_text)

        VAL_MY_PAGE_SCREEN ->
            text =
                context.stringOf(R.string.nickname_update_complete_btn_text)
    }
}

@BindingAdapter("switchCompleteButtonBackground")
fun AppCompatButton.switchCompleteButtonBackground(isValidNickname: Boolean) {
    if (isValidNickname) {
        background = context.drawableOf(R.drawable.shape_yellow_fill_10_rect)
        setTextColor(context.colorOf(R.color.gray_900))
    } else {
        background = context.drawableOf(R.drawable.shape_gray200_fill_10_rect)
        setTextColor(context.colorOf(R.color.gray_500))
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

@BindingAdapter("notiType")
fun TextView.setNotiType(notiType: String) {
    val resourceId = when (notiType) {
        "RANKUPTO2", "RANKUPTO3", "RANKUPTO4" -> R.string.notification_rankup
        "DELETERANKDOWNTO1", "DELETERANKDOWNTO2", "DELETERANKDOWNTO3" -> R.string.notification_rankdown
        "GOALFAILED" -> R.string.notification_goal_failed
        "LIKENOTI" -> R.string.notification_like
        "COMMENTNOTI" -> R.string.notification_comment
        "HOWTOLEVELUP" -> R.string.notification_how_to_levelup
        else -> null
    }

    if (resourceId != null) {
        text = context.getString(resourceId)
    } else {
        text = ""
    }
}

@BindingAdapter("notiType")
fun ImageView.setNotiType(notiType: String) {
    val drawableResourceId = when (notiType) {
        "RANKUPTO2", "DELETERANKDOWNTO2" -> R.drawable.ic_notification_lv2
        "RANKUPTO3", "DELETERANKDOWNTO3" -> R.drawable.ic_notification_lv3
        "RANKUPTO4" -> R.drawable.ic_notification_lv4
        "DELETERANKDOWNTO1" -> R.drawable.ic_notification_lv1
        "GOALFAILED", "HOWTOLEVELUP" -> R.drawable.ic_notification_logo
        "LIKENOTI" -> R.drawable.ic_notification_like
        "COMMENTNOTI" -> R.drawable.ic_notification_comment
        else -> 0
    }

    if (drawableResourceId != 0) {
        setImageResource(drawableResourceId)
    } else {
    }
}

@BindingAdapter("setLevelText")
fun TextView.setLevelText(level: Int?) {
    level?.let {
        when (it) {
            1 -> text = resources.getString(R.string.comment_level_1)
            2 -> text = resources.getString(R.string.comment_level_2)
            3 -> text = resources.getString(R.string.comment_level_3)
            4 -> text = resources.getString(R.string.comment_level_4)
        }
    }
}

@BindingAdapter("setWriterLevelImage")
fun CircleImageView.setWriterLevelImage(writerLevel: Int) {
    val drawableResId = when (writerLevel) {
        1 -> R.drawable.img_wineyfeed_profile_1
        2 -> R.drawable.img_wineyfeed_profile_2
        3 -> R.drawable.img_wineyfeed_profile_3
        4 -> R.drawable.img_wineyfeed_profile_4
        else -> R.drawable.img_wineyfeed_profile
    }
    setImageResource(drawableResId)
}
