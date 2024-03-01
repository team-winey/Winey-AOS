package org.go.sopt.winey.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.domain.repository.NotificationRepository
import org.go.sopt.winey.util.view.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _getUserState = MutableStateFlow<UiState<UserV2?>>(UiState.Empty)
    val getUserState: StateFlow<UiState<UserV2?>> = _getUserState.asStateFlow()

    private val _logoutState = MutableStateFlow<UiState<ResponseLogoutDto?>>(UiState.Empty)
    val logoutState: StateFlow<UiState<ResponseLogoutDto?>> = _logoutState.asStateFlow()

    private val _notiState = MutableStateFlow(true)
    val notiState: LiveData<Boolean> = _notiState.asLiveData()

    fun getUser() {
        viewModelScope.launch {
            _getUserState.value = UiState.Loading

            authRepository.getUser()
                .onSuccess { response ->
                    Timber.e("SUCCESS GET USER IN MAIN")
                    dataStoreRepository.saveUserInfo(response)
                    _getUserState.value = UiState.Success(response)
                    _getUserState.value = UiState.Empty
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패 ${t.code()}")
                        if (t.code() == CODE_TOKEN_EXPIRED || t.code() == CODE_INVALID_USER) {
                            postLogout()
                        }
                    }
                    Timber.e("${t.message}")
                    _getUserState.value = UiState.Failure("${t.message}")
                    _getUserState.value = UiState.Empty
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
                    Timber.e(response.message)
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

    fun getHasNewNoti() {
        viewModelScope.launch {
            notificationRepository.getHasNewNotification()
                .onSuccess { response ->
                    if (response?.hasNewNotification == true) {
                        _notiState.value = true
                        Timber.e("true")
                    } else {
                        _notiState.value = false
                        Timber.e("false")
                    }
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                }
        }
    }

    fun patchCheckAllNoti() {
        viewModelScope.launch {
            notificationRepository.patchCheckAllNotification()
                .onSuccess {
                    getHasNewNoti()
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                }
        }
    }

    fun patchFcmToken() {
        viewModelScope.launch {
            val token = dataStoreRepository.getDeviceToken().first()
            if (token.isNullOrBlank()) return@launch
            authRepository.patchFcmToken(token)
                .onSuccess {
                    Timber.e("디바이스 토큰 보내기 성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                }
        }
    }

    companion object {
        private const val CODE_TOKEN_EXPIRED = 401
        private const val CODE_INVALID_USER = 404
    }
}
