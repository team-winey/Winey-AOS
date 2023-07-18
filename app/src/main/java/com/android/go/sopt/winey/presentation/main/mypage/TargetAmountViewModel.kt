package com.android.go.sopt.winey.presentation.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TargetAmountViewModel : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: LiveData<String> = _amount
    val _day = MutableLiveData<String>()
    val day: LiveData<String> = _day
}