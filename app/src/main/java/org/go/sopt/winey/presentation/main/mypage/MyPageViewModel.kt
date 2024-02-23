package org.go.sopt.winey.presentation.main.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.util.view.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _deleteUserState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val deleteUserState: StateFlow<UiState<Unit>> = _deleteUserState.asStateFlow()

    fun deleteUser() {
        viewModelScope.launch {
            authRepository.deleteUser()
                .onSuccess { response ->
                    Timber.d("SUCCESS DELETE USER")
                    _deleteUserState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    _deleteUserState.value = UiState.Failure(t.message.toString())

                    if (t is HttpException) {
                        Timber.e("HTTP FAIL DELETE USER: ${t.code()} ${t.message}")
                        return@onFailure
                    }

                    Timber.e("FAIL DELETE USER: ${t.message}")
                }
        }
    }
}
