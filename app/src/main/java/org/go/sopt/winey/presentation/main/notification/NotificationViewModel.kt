package org.go.sopt.winey.presentation.main.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.entity.Notification
import org.go.sopt.winey.domain.repository.NotificationRepository
import org.go.sopt.winey.util.view.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _getNoticationState =
        MutableStateFlow<UiState<List<Notification>?>>(UiState.Loading)
    val getNotificationState: StateFlow<UiState<List<Notification>?>> =
        _getNoticationState.asStateFlow()

    fun getNotification() {
        viewModelScope.launch {
            notificationRepository.getNotification()
                .onSuccess { response ->
                    _getNoticationState.value = UiState.Success(response)
                    Timber.e("성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                    _getNoticationState.value = UiState.Failure("${t.message}")
                }
        }
    }
}
