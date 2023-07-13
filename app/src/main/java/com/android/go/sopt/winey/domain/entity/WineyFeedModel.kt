package com.android.go.sopt.winey.domain.entity

import androidx.annotation.DrawableRes

class WineyFeedModel(
    val feedMoney: Int,
    var likes: Long,
    @DrawableRes val feedImage: Int,
    var feedTitle: String,
    var nickName: String,
    @DrawableRes val profile: Int,
    var feedId: Int
)