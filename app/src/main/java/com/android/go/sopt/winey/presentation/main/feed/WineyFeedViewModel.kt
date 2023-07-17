package com.android.go.sopt.winey.presentation.main.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WineyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _WineyFeedListLiveData = MutableLiveData<List<WineyFeed>>()
    val WineyFeedListLiveData: List<WineyFeed>?
        get() = _WineyFeedListLiveData.value
    private val _getWineyFeedListState = MutableLiveData<UiState<List<WineyFeed>>>(UiState.Loading)
    val getWineyFeedListState: LiveData<UiState<List<WineyFeed>>>
        get() = _getWineyFeedListState

    init {
        getWineyFeed()
    }

    private fun getWineyFeed() {
        viewModelScope.launch {
            _getWineyFeedListState.value = UiState.Loading
            authRepository.getWineyFeed(1)
                .onSuccess { response ->
                    _getWineyFeedListState.value = UiState.Success(response)
                    _WineyFeedListLiveData.value = response
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        when (t.code()) {
                            CODE_WINEYFEED_INVALID_USER -> _getWineyFeedListState.value =
                                UiState.Failure(CODE_WINEYFEED_INVALID_USER.toString())

                            CODE_WINEYFEED_INVALID_REQUEST -> _getWineyFeedListState.value =
                                UiState.Failure(CODE_WINEYFEED_INVALID_USER.toString())

                            else -> _getWineyFeedListState.value =
                                UiState.Failure(MSG_WINEYFEED_FAIL)
                        }
                        Timber.e("$MSG_WINEYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    companion object {
        private const val CODE_WINEYFEED_INVALID_USER = 404
        private const val CODE_WINEYFEED_INVALID_REQUEST = 400
        private const val MSG_WINEYFEED_FAIL = "FAIL"
    }
}