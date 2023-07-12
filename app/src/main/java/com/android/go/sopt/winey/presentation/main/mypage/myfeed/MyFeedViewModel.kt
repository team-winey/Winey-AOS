package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.provider.Settings.Global.getString
import androidx.lifecycle.ViewModel
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.domain.model.WineyFeedModel

class MyFeedViewModel : ViewModel() {
    val dummyFeedList = listOf(
        WineyFeedModel(
            feedId = 1,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        ), WineyFeedModel(
            feedId = 2,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        ), WineyFeedModel(
            feedId = 3,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        ), WineyFeedModel(
            feedId = 4,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        ), WineyFeedModel(
            feedId = 5,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        ), WineyFeedModel(
            feedId = 6,
            feedImage = R.drawable.img_wineyfeed_default,
            feedMoney = 4000,
            feedTitle = "오늘은 자네가 자주 타는\n대중교통비를 줄여보는 거 어떤가?",
            profile = R.drawable.img_wineyfeed_profile,
            likes = 5319,
            nickName = "루이당 4세"
        )
    )
}