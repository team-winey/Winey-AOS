package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyFeedDialogViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _deleteMyFeedState = MutableLiveData<UiState<Unit>>(UiState.Loading)
    val deleteMyFeedState: LiveData<UiState<Unit>>
        get() = _deleteMyFeedState

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            authRepository.deleteFeed(feedId)
                .onSuccess { state ->
                    _deleteMyFeedState.value = UiState.Success(state)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _deleteMyFeedState.value.apply {
                            when (t.code()) {
                                CODE_MYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_MYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else -> _deleteMyFeedState.value =
                                    UiState.Failure(t.message())
                            }
                        }
                        Timber.e("${MSG_MYFEED_FAIL} : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    companion object {
        private const val CODE_MYFEED_INVALID_USER = 404
        private const val CODE_MYFEED_INVALID_REQUEST = 400
        private const val MSG_MYFEED_FAIL = "FAIL"
    }
}