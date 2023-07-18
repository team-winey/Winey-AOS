package com.android.go.sopt.winey.presentation.main.mypage.myfeed

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
class MyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _MyFeedListLiveData = MutableLiveData<List<WineyFeed>>()
    val MyFeedListLiveData: List<WineyFeed>?
        get() = _MyFeedListLiveData.value
    private val _getMyFeedListState = MutableLiveData<UiState<List<WineyFeed>>>(UiState.Loading)
    val getMyFeedListState: LiveData<UiState<List<WineyFeed>>>
        get() = _getMyFeedListState

    init {
        getMyFeed()
    }

    private fun getMyFeed() {
        viewModelScope.launch {
            authRepository.getMyFeedList(MYFEED_FEED_PAGE)
                .onSuccess { response ->
                    _getMyFeedListState.value = UiState.Success(response)
                    _MyFeedListLiveData.value = response
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        when (t.code()) {
                            CODE_MYFEED_INVALID_USER -> _getMyFeedListState.value =
                                UiState.Failure(t.message())

                            CODE_MYFEED_INVALID_REQUEST -> _getMyFeedListState.value =
                                UiState.Failure(t.message())

                            else -> _getMyFeedListState.value =
                                UiState.Failure(t.message())
                        }
                        Timber.e("$MSG_MYFEEDYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    companion object {
        private const val CODE_MYFEED_INVALID_USER = 404
        private const val CODE_MYFEED_INVALID_REQUEST = 400
        private const val MSG_MYFEEDYFEED_FAIL = "FAIL"
        private const val MYFEED_FEED_PAGE = 1
    }
}