package com.go.sopt.winey.presentation.main.mypage.myfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.go.sopt.winey.domain.entity.Like
import com.go.sopt.winey.domain.entity.WineyFeed
import com.go.sopt.winey.domain.repository.FeedRepository
import com.go.sopt.winey.util.view.UiState
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
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val _getMyFeedListState =
        MutableStateFlow<UiState<PagingData<WineyFeed>>>(UiState.Empty)
    val getMyFeedListState: StateFlow<UiState<PagingData<WineyFeed>>> =
        _getMyFeedListState.asStateFlow()

    private val _postMyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Empty)
    val postMyFeedLikeState: StateFlow<UiState<Like>> = _postMyFeedLikeState.asStateFlow()

    private val _deleteMyFeedState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val deleteMyFeedState: StateFlow<UiState<Unit>> = _deleteMyFeedState.asStateFlow()

    init {
        getMyFeed()
    }

    fun initDeleteFeedState() {
        _deleteMyFeedState.value = UiState.Empty
    }

    fun likeFeed(feedId: Int, isLiked: Boolean) {
        val requestPostLikeDto = RequestPostLikeDto(isLiked)
        postLike(feedId, requestPostLikeDto)
    }

    private fun postLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto) {
        viewModelScope.launch {
            _postMyFeedLikeState.emit(UiState.Loading)

            feedRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { response ->
                    _postMyFeedLikeState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_postMyFeedLikeState, t) }
        }
    }

    fun getMyFeed() {
        viewModelScope.launch {
            _getMyFeedListState.emit(UiState.Loading)

            feedRepository.getMyFeedList().cachedIn(viewModelScope)
                .collectLatest { response ->
                    _getMyFeedListState.emit(UiState.Success(response))
                }
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            feedRepository.deleteFeed(feedId)
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
