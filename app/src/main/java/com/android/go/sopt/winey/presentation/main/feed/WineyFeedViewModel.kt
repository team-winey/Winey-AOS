package com.android.go.sopt.winey.presentation.main.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.entity.WineyFeedModel
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WineyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _WineyFeedListLiveData = MutableLiveData<List<WineyFeedModel>>()
    val WineyFeedListLiveData: List<WineyFeedModel>?
        get() = _WineyFeedListLiveData.value
    private val _getListState = MutableLiveData<UiState<List<WineyFeedModel>>>(UiState.Loading)
    val getListState: LiveData<UiState<List<WineyFeedModel>>>
        get() = _getListState

    init {
        getWineyFeed()
    }

    private fun getWineyFeed() {
        viewModelScope.launch {
            _getListState.value = UiState.Loading
            authRepository.getWineyFeed(1)
                .onSuccess { response ->
                    _getListState.value = UiState.Success(response)
                    _WineyFeedListLiveData.value = response
                }
                .onFailure {
                    _getListState.value = UiState.Failure(WINEYFEED_FAIL_MSG)
                }
        }
    }
    companion object {
        private const val WINEYFEED_FAIL_MSG = "getWineyFeed fail"
    }
}