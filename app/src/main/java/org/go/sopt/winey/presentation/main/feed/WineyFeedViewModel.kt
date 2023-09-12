package org.go.sopt.winey.presentation.main.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
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

    private val _postWineyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Empty)
    val postWineyFeedLikeState: StateFlow<UiState<Like>> = _postWineyFeedLikeState.asStateFlow()

    private val _deleteWineyFeedState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val deleteWineyFeedState: StateFlow<UiState<Unit>> = _deleteWineyFeedState.asStateFlow()

    private val _isItemClicked = MutableStateFlow(false)
    val isItemClicked: StateFlow<Boolean> = _isItemClicked.asStateFlow()

    init {
        getWineyFeedList()
    }

    fun updateItemClickedState(clicked: Boolean) {
        _isItemClicked.value = clicked
    }

    fun getWineyFeedList() {
        viewModelScope.launch {
            _getWineyFeedListState.emit(UiState.Loading)

            // todo: 캐싱을 하면 항상 서버통신 결과가 프래그먼트에서 수집되어서
            //  위니피드에서 삭제했던 아이템이 돌아가면 다시 뜬다...
            feedRepository.getWineyFeedList().cachedIn(viewModelScope).collectLatest { response ->
                Timber.e("PAGING DATA COLLECT in ViewModel")
                _getWineyFeedListState.emit(UiState.Success(response))
            }
        }
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

    fun initDeleteFeedState() {
        _deleteWineyFeedState.value = UiState.Empty
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
