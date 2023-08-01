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
        val requestBody = RequestCreateGoalDto(
            targetMoney = _amount.value?.replace(",", "")!!.toInt(),
            targetDay = _day.value?.replace(",", "")!!.toInt()
        )
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
        if (day == "1000000000") {
            _dayCheck.value = false
        } else {
            val dayValue = day.toLongOrNull()
            _dayCheck.value = dayValue != null && (dayValue in 0..4 || dayValue > 365)
        }
    }

    fun checkAmount(amount: String) {
        if (amount == "1000000000") {
            _amountCheck.value = false
        } else {
            val amountValue = amount.toLongOrNull()
            _amountCheck.value = amountValue != null && amountValue in 0 until 30000
        }
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
    }
}
