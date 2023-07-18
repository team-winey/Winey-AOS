package com.android.go.sopt.winey.presentation.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TargetAmountViewModel : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: LiveData<String> = _amount
    val _day = MutableLiveData<String>()
    val day: LiveData<String> = _day

    private val _amountCheck = MutableLiveData<Boolean>()
    val amountCheck: LiveData<Boolean> = _amountCheck

    private val _dayCheck = MutableLiveData<Boolean>()
    val dayCheck: LiveData<Boolean> = _dayCheck

    private val _buttonStatecheck = MutableLiveData<Boolean>()
    val buttonStateCheck: LiveData<Boolean> = _buttonStatecheck

    fun checkDay(Day: String) {
        if (Day.toLong() > 0 && Day.toLong() < 5 && !Day.isNullOrEmpty()) {
            _dayCheck.value = true
        } else {
            _dayCheck.value = false
        }
    }

    fun checkAmount(Amount: String) {
        if (Amount.toLong() > 0 && Amount.toLong() < 30000 && !Amount.isNullOrEmpty()) {
            _amountCheck.value = true
        } else {
            _amountCheck.value = false
        }
    }

    fun checkButtonState() {
        if (!_amount.value.isNullOrEmpty() && !_day.value.isNullOrEmpty() && _dayCheck.value == false && _amountCheck.value == false) {
            _buttonStatecheck.value = true
        } else {
            _buttonStatecheck.value = false
        }
    }

    companion object {
        const val MAX_AMOUNT_LENGTH = 12
    }
}