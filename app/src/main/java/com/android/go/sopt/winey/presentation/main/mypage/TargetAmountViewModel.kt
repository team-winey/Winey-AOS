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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TargetAmountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
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

    private val _createGoalState = MutableLiveData<UiState<Goal>>()
    val createGoalState: LiveData<UiState<Goal>> = _createGoalState

    private val _requestBody = MutableLiveData<RequestCreateGoalDto>()
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

    fun checkDay(Day: String) {
        if (Day.toLong() >= 0 && Day.toLong() < 5 && !Day.isNullOrEmpty() || Day.toLong()>365) {
            _dayCheck.value = true
        } else {
            _dayCheck.value = false
        }

        if (Day.equals("1000000000")) {
            _amountCheck.value = false
        }
    }

    fun checkAmount(Amount: String) {
        if (Amount.toLong() >= 0 && Amount.toLong() < 30000 && !Amount.isNullOrEmpty()) {
            _amountCheck.value = true
        } else {
            _amountCheck.value = false
        }

        if (Amount.equals("1000000000")) {
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