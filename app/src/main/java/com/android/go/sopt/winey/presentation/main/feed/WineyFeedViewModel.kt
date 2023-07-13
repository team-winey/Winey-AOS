package com.android.go.sopt.winey.presentation.main.feed

import androidx.lifecycle.ViewModel
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.domain.entity.WineyFeedModel

class WineyFeedViewModel : ViewModel() {
    val dummyFeedList = listOf(
        WineyFeedModel(
            feedId = 1,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        ), WineyFeedModel(
            feedId = 2,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        ), WineyFeedModel(
            feedId = 3,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        ), WineyFeedModel(
            feedId = 4,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        ), WineyFeedModel(
            feedId = 5,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        ), WineyFeedModel(
            feedId = 6,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "교통비를 아끼기 위해 대중교통 대신\n걸어서 출퇴근을 했습니다",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "부자가 되고싶어"
        )
    )
}