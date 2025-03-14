package org.go.sopt.winey.util.binding

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import de.hdodenhof.circleimageview.CircleImageView
import org.go.sopt.winey.R
import org.go.sopt.winey.presentation.model.UserLevel
import org.go.sopt.winey.presentation.model.WineyFeedType
import org.go.sopt.winey.presentation.nickname.NicknameActivity.Companion.MY_PAGE_SCREEN
import org.go.sopt.winey.presentation.nickname.NicknameActivity.Companion.STORY_SCREEN
import org.go.sopt.winey.util.context.colorOf
import org.go.sopt.winey.util.context.drawableOf
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.number.formatAmountNumber
import org.go.sopt.winey.util.state.InputError
import org.go.sopt.winey.util.state.InputUiState
import org.go.sopt.winey.util.state.InputUiState.*
import java.text.DecimalFormat

@BindingAdapter("likedAmount")
fun applyNumberFormat(view: TextView, amount: Long) {
    val context = view.context ?: return
    val decimalFormat = DecimalFormat("#,###")
    val formattedValue = when {
        amount >= 1E9 -> "${(amount.toFloat() / 1E9).toInt()}B"
        amount >= 1E6 -> "${(amount.toFloat() / 1E6).toInt()}M"
        amount >= 1E3 -> "${(amount.toFloat() / 1E3).toInt()}K"
        else -> decimalFormat.format(amount)
    }
    view.text = context.getString(R.string.wineyfeed_item_like_number, formattedValue)
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

@BindingAdapter("switchUploadImageTitle")
fun TextView.switchUploadImageTitle(feedType: WineyFeedType) {
    text = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_photo_title, "절약을 실천한")
        WineyFeedType.CONSUME -> context.getString(R.string.upload_photo_title, "과소비한")
    }
}

@BindingAdapter("switchUploadImageButtonText")
fun TextView.switchUploadImageButtonText(feedType: WineyFeedType) {
    text = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_plus_text, "절약을")
        WineyFeedType.CONSUME -> context.getString(R.string.upload_plus_text, "과소비를")
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

@BindingAdapter("switchUploadContentTitle")
fun TextView.switchUploadContentTitle(feedType: WineyFeedType) {
    text = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_plus_text, "절약을")
        WineyFeedType.CONSUME -> context.getString(R.string.upload_plus_text, "과소비를")
    }
}

@BindingAdapter("switchUploadContentHint")
fun EditText.switchUploadContentHint(feedType: WineyFeedType) {
    hint = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_save_content_edittext_hint)
        WineyFeedType.CONSUME -> context.getString(R.string.upload_consume_content_edittext_hint)
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
        if (inputUiState.error == InputError.Upload) {
            text = context.stringOf(R.string.upload_content_error_text)
        }
    }
}

@BindingAdapter("switchUploadAmountTitle")
fun TextView.switchUploadAmountTitle(feedType: WineyFeedType) {
    text = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_amount_title, "절약")
        WineyFeedType.CONSUME -> context.getString(R.string.upload_amount_title, "과소비")
    }
}

@BindingAdapter("switchUploadAmountDetailText")
fun TextView.switchUploadAmountDetailText(feedType: WineyFeedType) {
    text = when (feedType) {
        WineyFeedType.SAVE -> context.getString(R.string.upload_amount_detail, "절약하신")
        WineyFeedType.CONSUME -> context.getString(R.string.upload_amount_detail, "과소비한")
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
            text = when (inputUiState.error) {
                InputError.Nickname.BLANK_INPUT -> context.stringOf(R.string.nickname_blank_input_error)
                InputError.Nickname.INVALID_CHAR -> context.stringOf(R.string.nickname_space_special_char_error)
                InputError.Nickname.UNCHECKED_DUPLICATION -> context.stringOf(R.string.nickname_unchecked_duplication_error)
                InputError.Nickname.DUPLICATED -> context.stringOf(R.string.nickname_duplicated_error)
                else -> ""
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
        STORY_SCREEN -> {
            text = context.getString(R.string.nickname_counter, inputNicknameLength)
        }

        MY_PAGE_SCREEN -> {
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
        STORY_SCREEN -> visibility = View.GONE
        MY_PAGE_SCREEN -> visibility = View.VISIBLE
    }
}

@BindingAdapter("switchTitleText")
fun TextView.switchTitleText(prevScreenName: String) {
    when (prevScreenName) {
        STORY_SCREEN ->
            text =
                context.stringOf(R.string.nickname_default_title)

        MY_PAGE_SCREEN ->
            text =
                context.stringOf(R.string.nickname_mypage_title)
    }
}

@BindingAdapter("switchCompleteButtonText")
fun TextView.switchCompleteButtonText(prevScreenName: String) {
    when (prevScreenName) {
        STORY_SCREEN ->
            text =
                context.stringOf(R.string.nickname_start_btn_text)

        MY_PAGE_SCREEN ->
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

@BindingAdapter("setMyPageLevelResource", "imageViewId")
fun ImageView.setMyPageLevelResource(userLevel: String, imageViewId: String) {
    userLevel.let { userLevel ->
        imageViewId.let { imageViewId ->
            val imageResource: Int = when (imageViewId) {
                "SAVER" -> {
                    when (userLevel) {
                        "평민" -> R.drawable.img_mypage_saver_lv1
                        "기사" -> R.drawable.img_mypage_saver_lv2
                        "귀족" -> R.drawable.img_mypage_saver_lv3
                        "황제" -> R.drawable.img_mypage_saver_lv4
                        else -> {
                            R.drawable.img_mypage_saver_lv1
                        }
                    }
                }

                else -> {
                    R.drawable.img_mypage_saver_lv1
                }
            }
            setImageResource(imageResource)
        }
    }
}

@BindingAdapter("setMyPageLevelBackground")
fun ConstraintLayout.setMyPageLevelBackground(userLevel: String) {
    val resourceId: Int = when (userLevel) {
        "평민" -> R.drawable.img_mypage_background_lv1
        "기사" -> R.drawable.img_mypage_background_lv2
        "귀족" -> R.drawable.img_mypage_background_lv3
        "황제" -> R.drawable.img_mypage_background_lv4
        else -> {
            R.drawable.img_mypage_background_lv1
        }
    }
    this.setBackgroundResource(resourceId)
}

@BindingAdapter("setMyPageItemIcon")
fun ImageView.setMyPageItemIcon(iconType: String?) {
    iconType?.let {
        val drawableResId = when (it) {
            "AMERICANO" -> R.drawable.ic_mypage_coffee
            "SNEAKERS" -> R.drawable.ic_mypage_sneakers
            "AIRPODS" -> R.drawable.ic_mypage_airpods
            "CHICKEN" -> R.drawable.ic_mypage_chicken
            else -> {
                R.drawable.ic_mypage_chicken
            }
        }
        setImageResource(drawableResId)
    }
}

@BindingAdapter("setMyPageItemDescription")
fun TextView.setMyPageItemDescription(iconType: String?) {
    text = iconType?.let {
        when (it) {
            "AMERICANO" -> context.getString(R.string.mypage_description_eat, "아메리카노를")
            "SNEAKERS" -> context.getString(R.string.mypage_description_buy, "운동화를")
            "AIRPODS" -> context.getString(R.string.mypage_description_buy, "에어팟을")
            "CHICKEN" -> context.getString(R.string.mypage_description_eat, "치킨을")
            else -> " "
        }
    } ?: " "
}

@BindingAdapter("setMyPageItemSavedMoney", "iconType")
fun TextView.setMyPageItemSavedMoney(savedAmount: Int, iconType: String) {
    val context = this.context ?: return
    val money: String = when (iconType) {
        "AMERICANO" -> context.stringOf(R.string.mypage_americano_money)
        "CHICKEN" -> context.stringOf(R.string.mypage_chicken_money)
        "SNEAKERS" -> context.stringOf(R.string.mypage_sneakers_money)
        "AIRPODS" -> context.stringOf(R.string.mypage_airpods_money)
        else -> ""
    }

    text = money
}

@BindingAdapter("setMyPageItemSavedAmount", "iconType")
fun TextView.setMyPageItemSavedAmount(savedAmount: Int, iconType: String) {
    val amount: String = when (iconType) {
        "AMERICANO" -> (savedAmount / 5000).toString()
        "SNEAKERS" -> (savedAmount / 150000).toString()
        "AIRPODS" -> (savedAmount / 300000).toString()
        "CHICKEN" -> (savedAmount / 30000).toString()
        else -> ""
    }

    text = amount
}

@BindingAdapter("setMyPageItemSavedMeasurement", "iconType")
fun TextView.setMyPageItemSavedMeasurement(savedAmount: Int, iconType: String) {
    val context = this.context ?: return
    val measurement: String = when (iconType) {
        "AMERICANO" -> context.stringOf(R.string.mypage_americano_measurement)
        "SNEAKERS" -> context.stringOf(R.string.mypage_sneakers_measurement)
        "AIRPODS" -> context.stringOf(R.string.mypage_airpods_measurement)
        "CHICKEN" -> context.stringOf(R.string.mypage_chicken_measurement)
        else -> ""
    }

    text = measurement
}

@BindingAdapter("switchFeedTypeBackground")
fun LinearLayout.switchFeedTypeBackground(feedType: String) {
    val context = this.context ?: return
    background = when (feedType) {
        WineyFeedType.CONSUME.name -> {
            context.drawableOf(R.drawable.shape_red500_line_6_rect)
        }

        else -> {
            context.drawableOf(R.drawable.shape_green500_line_6_rect)
        }
    }
}

@BindingAdapter("switchFeedTypeText")
fun TextView.switchFeedTypeText(feedType: String) {
    val context = this.context ?: return
    when (feedType) {
        WineyFeedType.CONSUME.name -> {
            text = context.stringOf(R.string.wineyfeed_feed_type_consume)
            setTextColor(context.colorOf(R.color.sub_red_500))
        }

        else -> {
            text = context.stringOf(R.string.wineyfeed_feed_type_save)
            setTextColor(context.colorOf(R.color.sub_green_500))
        }
    }
}

@BindingAdapter("feedType", "feedMoney")
fun TextView.switchFeedMoney(feedType: String, feedMoney: Long) {
    val context = this.context ?: return
    val formattedMoney = feedMoney.toInt().formatAmountNumber()
    text = when (feedType) {
        WineyFeedType.CONSUME.name -> {
            context.getString(R.string.wineyfeed_item_consume_money, formattedMoney)
        }

        else -> {
            context.getString(R.string.wineyfeed_item_save_money, formattedMoney)
        }
    }
}

@BindingAdapter("setProgressBarTitle")
fun TextView.setProgressBarTitle(currentLevel: String) {
    val context = this.context ?: return

    if (currentLevel == UserLevel.FOURTH.rankName) {
        text = context.getString(R.string.wineyfeed_goal_progressbar_lv4_title)
        return
    }

    val currentLevels = context.resources.getStringArray(R.array.user_level)
    val nextLevels = listOf("기사가", "귀족이", "황제가")
    currentLevels.forEachIndexed { index, level ->
        if (level == currentLevel) {
            text = context.getString(
                R.string.wineyfeed_goal_progressbar_title,
                nextLevels[index]
            )
            return
        }
    }
}

@BindingAdapter("userLevel", "accumulatedAmount")
fun TextView.setCurrentMoney(userLevel: String, accumulatedAmount: Int) {
    val context = this.context ?: return

    text = if (userLevel == UserLevel.FOURTH.rankName) {
        context.getString(R.string.wineyfeed_goal_progressbar_lv4_subTitle)
    } else {
        context.getString(
            R.string.wineyfeed_goal_progressbar_current_money,
            accumulatedAmount.formatAmountNumber()
        )
    }
}

@BindingAdapter("setTargetMoney")
fun TextView.setTargetMoney(currentLevel: String) {
    val context = this.context ?: return
    isVisible = currentLevel != UserLevel.FOURTH.rankName

    val userLevels = context.resources.getStringArray(R.array.user_level)
    val targetMoneys = context.resources.getIntArray(R.array.target_money)
    userLevels.forEachIndexed { index, level ->
        if (level == currentLevel) {
            text = context.getString(
                R.string.wineyfeed_goal_progressbar_target_money,
                targetMoneys[index]
            )
            return
        }
    }
}
