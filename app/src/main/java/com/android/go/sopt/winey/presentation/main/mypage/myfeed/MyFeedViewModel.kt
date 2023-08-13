package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _getMyFeedListState =
        MutableStateFlow<UiState<PagingData<WineyFeed>>>(UiState.Loading)
    val getMyFeedListState: StateFlow<UiState<PagingData<WineyFeed>>> =
        _getMyFeedListState.asStateFlow()

    private val _postMyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Loading)
    val postMyFeedLikeState: StateFlow<UiState<Like>> = _postMyFeedLikeState.asStateFlow()

    val _deleteMyFeedState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val deleteMyFeedState: StateFlow<UiState<Unit>> = _deleteMyFeedState.asStateFlow()

    init {
        getMyFeed()
    }

    fun likeFeed(feedId: Int, isLiked: Boolean) {
        val requestPostLikeDto = RequestPostLikeDto(isLiked)
        postLike(feedId, requestPostLikeDto)
    }

    private fun postLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto) {
        viewModelScope.launch {
            authRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { response ->
                    _postMyFeedLikeState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_postMyFeedLikeState, t) }
        }
    }

    fun getMyFeed() {
        viewModelScope.launch {
            _getMyFeedListState.emit(UiState.Loading)
            authRepository.getMyFeedList().cachedIn(viewModelScope)
                .collectLatest { response ->
                    _getMyFeedListState.emit(UiState.Success(response))
                }
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            authRepository.deleteFeed(feedId)
                .onSuccess { response ->
                    _deleteMyFeedState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_deleteMyFeedState, t) }
        }
    }

    private fun <T> handleFailureState(loadingState: MutableStateFlow<UiState<T>>, t: Throwable) {
        if (t is HttpException) {
            val errorMessage = when (t.code()) {
                CODE_MYFEED_INVALID_USER -> t.message()
                CODE_MYFEED_INVALID_REQUEST -> t.message()
                else -> t.message()
            }
            loadingState.value = UiState.Failure(errorMessage)
            Timber.e("$MSG_MYFEED_FAIL : ${t.code()} : ${t.message()}")
        }
    }

    companion object {
        private const val CODE_MYFEED_INVALID_USER = 404
        private const val CODE_MYFEED_INVALID_REQUEST = 400
        private const val MSG_MYFEED_FAIL = "FAIL"
    }
}
