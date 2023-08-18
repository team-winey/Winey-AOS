package com.android.go.sopt.winey.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _getUserState = MutableStateFlow<UiState<User?>>(UiState.Loading)
    val getUserState: StateFlow<UiState<User?>> = _getUserState.asStateFlow()

    private val _logoutState = MutableStateFlow<UiState<ResponseLogoutDto?>>(UiState.Empty)
    val logoutState: StateFlow<UiState<ResponseLogoutDto?>> = _logoutState.asStateFlow()

    fun getUser() {
        viewModelScope.launch {
            _getUserState.value = UiState.Loading

            authRepository.getUser()
                .onSuccess { response ->
                    dataStoreRepository.saveUserInfo(response)
                    _getUserState.value = UiState.Success(response)
                    Timber.e("메인뷰모델 성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패 ${t.code()}")
                        if (t.code() == CODE_TOKEN_EXPIRED) {
                            postLogout()
                        }
                    }
                    Timber.e("${t.message}")
                    _getUserState.value = UiState.Failure("${t.message}")
                }
        }
    }

    fun postLogout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading

            authRepository.postLogout()
                .onSuccess { response ->
                    dataStoreRepository.saveAccessToken("", "")
                    _logoutState.value = UiState.Success(response)
                    Timber.e("${response.message}")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                    _logoutState.value = UiState.Failure("${t.message}")
                }
        }
    }

    companion object {
        private const val CODE_TOKEN_EXPIRED = 401
    }
}
