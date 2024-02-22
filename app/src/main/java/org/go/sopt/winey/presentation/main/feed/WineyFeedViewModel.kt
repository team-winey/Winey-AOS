package org.go.sopt.winey.presentation.main.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import org.go.sopt.winey.data.model.remote.response.ResponseDeleteFeedDto
import org.go.sopt.winey.domain.entity.DetailFeed
import org.go.sopt.winey.domain.entity.Like
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.domain.repository.FeedRepository
import org.go.sopt.winey.util.view.UiState
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

    private val _getDetailFeedState =
        MutableStateFlow<UiState<DetailFeed?>>(UiState.Empty)
    val getDetailFeedState: StateFlow<UiState<DetailFeed?>> =
        _getDetailFeedState.asStateFlow()

    private val _postWineyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Empty)
    val postWineyFeedLikeState: StateFlow<UiState<Like>> = _postWineyFeedLikeState.asStateFlow()

    private val _deleteWineyFeedState =
        MutableStateFlow<UiState<ResponseDeleteFeedDto?>>(UiState.Empty)
    val deleteWineyFeedState: StateFlow<UiState<ResponseDeleteFeedDto?>> =
        _deleteWineyFeedState.asStateFlow()

    init {
        getWineyFeedList()
    }

    private fun getWineyFeedList() {
        viewModelScope.launch {
            _getWineyFeedListState.emit(UiState.Loading)

            feedRepository.getWineyFeedList().cachedIn(viewModelScope)
                .catch { t ->
                    handleFailureState(_getWineyFeedListState, t)
                }
                .collectLatest { pagingData ->
                    _getWineyFeedListState.emit(UiState.Success(pagingData))
                }
        }
    }

    fun getDetailFeed(feedId: Int) {
        viewModelScope.launch {
            _getDetailFeedState.emit(UiState.Loading)

            feedRepository.getFeedDetail(feedId)
                .onSuccess { response ->
                    _getDetailFeedState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_getDetailFeedState, t) }
        }
    }

    fun likeFeed(feedId: Int, isLiked: Boolean) {
        val requestPostLikeDto = RequestPostLikeDto(isLiked)
        postLike(feedId, requestPostLikeDto)
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

    fun initDeleteFeedState() {
        _deleteWineyFeedState.value = UiState.Empty
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
        private const val ERROR_GET_WINEY_FEED_LIST = "error get winey feed list"
    }
}
