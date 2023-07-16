package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AmountViewModel : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: String get() = _amount.value ?: ""

    val isValidAmount: LiveData<Boolean> = _amount.map { validateLength(it) }

    private fun validateLength(amount: String): Boolean =
        amount.length in MIN_AMOUNT_LENGTH..MAX_AMOUNT_LENGTH

    companion object {
        const val MIN_AMOUNT_LENGTH = 1
        const val MAX_AMOUNT_LENGTH = 23
    }
}