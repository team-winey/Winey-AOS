package com.android.go.sopt.winey.presentation.main.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.UiState.Failure
import com.android.go.sopt.winey.util.view.UiState.Success
import com.android.go.sopt.winey.util.view.UiState.Loading
import com.android.go.sopt.winey.util.view.UiState.Empty
import com.android.go.sopt.winey.util.view.UiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _getUserResult: MutableLiveData<ResponseGetUserDto> = MutableLiveData()
    val getUserResult: LiveData<ResponseGetUserDto> get() = _getUserResult

    private val _getUserState: MutableLiveData<UiState<ResponseGetUserDto>> = MutableLiveData()
    val getUserState: LiveData<UiState<ResponseGetUserDto>> get() = _getUserState

    private val _nickname = MutableLiveData<String>()
    private val _targetMoney = MutableLiveData<Int>()
    private val _targetDay = MutableLiveData<Int>()
    private val _duringGoalAmount = MutableLiveData<Int>()
    private val _duringGoalCount = MutableLiveData<Int>()

    val nickname : LiveData<String> get() = _nickname
    val targetMoney : LiveData<Int> get() = _targetMoney
    val targetDay : LiveData<Int> get() = targetDay
    val duringGoalAmount : LiveData<Int> get() = _duringGoalAmount
    val duringGoalCount : LiveData<Int> get() = _duringGoalCount
    init {
        _nickname.value = "왕자"
    }

    fun getUser(){
        viewModelScope.launch {
            kotlin.runCatching {

            }
            authRepository.getUser(1)
                .onSuccess { response->
                    if(response == null){
                        _getUserState.value = Failure("fail")
                        Log.e("sangwook","fail")
                        return@onSuccess
                    }
                    _getUserResult.value = response
                    _nickname.value = response.data.user.nickname
                    _getUserState.value = Success(response)
                    Log.e("sangwook","success")
                }
                .onFailure { t->
                    if(t is HttpException){
                        _getUserState.value = Empty
                        Log.e("sangwook","empty")
                    }
                    Log.e("sangwook","empty")
                }
        }
    }
}