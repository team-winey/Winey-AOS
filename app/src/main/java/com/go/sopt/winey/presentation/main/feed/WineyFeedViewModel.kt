package com.go.sopt.winey.presentation.main.feed

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
class WineyFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val _getWineyFeedListState =
        MutableStateFlow<UiState<PagingData<WineyFeed>>>(UiState.Empty)
    val getWineyFeedListState: StateFlow<UiState<PagingData<WineyFeed>>> =
        _getWineyFeedListState.asStateFlow()

    private val _postWineyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Empty)
    val postWineyFeedLikeState: StateFlow<UiState<Like>> = _postWineyFeedLikeState.asStateFlow()

    private val _deleteWineyFeedState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val deleteWineyFeedState: StateFlow<UiState<Unit>> = _deleteWineyFeedState.asStateFlow()

    fun initDeleteFeedState() {
        _deleteWineyFeedState.value = UiState.Empty
    }

    init {
        getWineyFeed()
    }

    fun likeFeed(feedId: Int, isLiked: Boolean) {
        val requestPostLikeDto = RequestPostLikeDto(isLiked)
        postLike(feedId, requestPostLikeDto)
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            _deleteWineyFeedState.emit(UiState.Loading)

            feedRepository.deleteFeed(feedId)
                .onSuccess { response ->
                    _deleteWineyFeedState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_deleteWineyFeedState, t) }
        }
    }

    private fun postLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto) {
        viewModelScope.launch {
            _postWineyFeedLikeState.emit(UiState.Loading)

            feedRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { response ->
                    _postWineyFeedLikeState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_postWineyFeedLikeState, t) }
        }
    }

    fun getWineyFeed() {
        viewModelScope.launch {
            _getWineyFeedListState.emit(UiState.Loading)

            feedRepository.getWineyFeedList().cachedIn(viewModelScope)
                .collectLatest { response ->
                    _getWineyFeedListState.emit(UiState.Success(response))
                }
        }
    }

    private fun <T> handleFailureState(loadingState: MutableStateFlow<UiState<T>>, t: Throwable) {
        if (t is HttpException) {
            val errorMessage = when (t.code()) {
                CODE_WINEYFEED_INVALID_USER -> t.message()
                CODE_WINEYFEED_INVALID_REQUEST -> t.message()
                else -> t.message()
            }
            loadingState.value = UiState.Failure(errorMessage)
            Timber.e("$MSG_WINEYFEED_FAIL : ${t.code()} : ${t.message()}")
        }
    }

    companion object {
        private const val CODE_WINEYFEED_INVALID_USER = 404
        private const val CODE_WINEYFEED_INVALID_REQUEST = 400
        private const val MSG_WINEYFEED_FAIL = "FAIL"
    }
}
