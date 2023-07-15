package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

// 1000 단위에 따라 콤마 표시
// 내용 채워지면 버튼 활성화 (액티비티 참조)
class AmountViewModel : ViewModel() {
    val _amount = MutableLiveData<String>()
    private val amount: String get() = _amount.value ?: ""

    val isValidAmount: LiveData<Boolean> = _amount.map { validateLength(it) }

    private fun validateLength(amount: String): Boolean =
        amount.length in MIN_AMOUNT_LENGTH..MAX_AMOUNT_LENGTH

    companion object {
        const val MIN_AMOUNT_LENGTH = 1
        const val MAX_AMOUNT_LENGTH = 23
    }
}