package com.android.go.sopt.winey.presentation.main.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _getUserResult = MutableLiveData<ResponseGetUserDto>()
    val getUserResult: LiveData<ResponseGetUserDto> get() = _getUserResult

    private val _getUserState = MutableLiveData<UiState<User>>(UiState.Loading)
    val getUserState: LiveData<UiState<User>> get() = _getUserState

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _getUserState.value = UiState.Loading
            authRepository.getUser()
                .onSuccess { response ->
                    _getUserState.value = UiState.Success(response)
                    Log.e("test log", "성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Log.e("test log", "HTTP 실패")
                    }
                    Log.e("test log", "${t.message}")
                    _getUserState.value = UiState.Failure("${t.message}")
                }
        }
    }
}