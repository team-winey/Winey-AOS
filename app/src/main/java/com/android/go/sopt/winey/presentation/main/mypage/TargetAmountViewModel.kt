package com.android.go.sopt.winey.presentation.main.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TargetAmountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    val _day = MutableLiveData<String>()
    val day: LiveData<String> get() = _day

    private val _amountCheck = MutableStateFlow<Boolean>(false)
    val amountCheck: StateFlow<Boolean> get() = _amountCheck.asStateFlow()

    private val _dayCheck = MutableStateFlow<Boolean>(false)
    val dayCheck: StateFlow<Boolean> get() = _dayCheck.asStateFlow()

    private val _buttonStatecheck = MutableStateFlow<Boolean>(false)
    val buttonStateCheck: StateFlow<Boolean> get() = _buttonStatecheck.asStateFlow()

    private val _createGoalState = MutableStateFlow<UiState<Goal>>(UiState.Empty)
    val createGoalState: StateFlow<UiState<Goal>> get() = _createGoalState.asStateFlow()

    fun postCreateGoal() {
        val money: Any? = amount.value
        val day: Any? = day.value
        val requestBody: RequestCreateGoalDto
        if (money is String && day is String) {
            requestBody = RequestCreateGoalDto(
                targetMoney = money.replace(",", "").toInt(),
                targetDay = day.replace(",", "").toInt()
            )
        } else {
            return
        }
        viewModelScope.launch {
            _createGoalState.value = UiState.Loading

            authRepository.postCreateGoal(requestBody)
                .onSuccess { response ->
                    _createGoalState.value = UiState.Success(response)
                    Log.e("test log", "목표설정 성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Log.e("test log", "HTTP 실패")
                    }
                    Log.e("test log", "${t.message}")
                    _createGoalState.value = UiState.Failure("${t.message}")
                }
        }
    }

    fun checkDay(day: String) {
        if (day == MAX) {
            _dayCheck.value = false
            return
        }
        val dayValue = day.toLongOrNull()
        _dayCheck.value =
            dayValue != null && (dayValue in BASE_VALUE until MIN_DAY_VALUE || dayValue > MAX_DAY_VALUE)
    }

    fun checkAmount(amount: String) {
        if (amount == MAX) {
            _amountCheck.value = false
            return
        }
        val amountValue = amount.toLongOrNull()
        _amountCheck.value =
            amountValue != null && amountValue in BASE_VALUE until MIN_AMOUNT_VALUE
    }

    fun checkButtonState() {
        val day = _day.value
        val amount = _amount.value
        _buttonStatecheck.value =
            !day.isNullOrEmpty() && !amount.isNullOrEmpty() && _dayCheck.value && _amountCheck.value
    }

    companion object {
        const val MAX_AMOUNT_LENGTH = 12
        const val MAX_DAY_LENGTH = 3
        const val MIN_AMOUNT_VALUE = 30000
        const val MIN_DAY_VALUE = 5
        const val MAX_DAY_VALUE = 365
        const val BASE_VALUE = 0
        const val MAX = "1000000000"
    }
}
