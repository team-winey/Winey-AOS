package org.go.sopt.winey.presentation.main.mypage.myfeed

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
class MyFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val _getMyFeedListState =
        MutableStateFlow<UiState<PagingData<WineyFeed>>>(UiState.Empty)
    val getMyFeedListState: StateFlow<UiState<PagingData<WineyFeed>>> =
        _getMyFeedListState.asStateFlow()

    private val _getDetailFeedState =
        MutableStateFlow<UiState<DetailFeed?>>(UiState.Empty)
    val getDetailFeedState: StateFlow<UiState<DetailFeed?>> =
        _getDetailFeedState.asStateFlow()

    private val _postMyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Empty)
    val postMyFeedLikeState: StateFlow<UiState<Like>> = _postMyFeedLikeState.asStateFlow()

    private val _deleteMyFeedState =
        MutableStateFlow<UiState<ResponseDeleteFeedDto?>>(UiState.Empty)
    val deleteMyFeedState: StateFlow<UiState<ResponseDeleteFeedDto?>> =
        _deleteMyFeedState.asStateFlow()

    init {
        getMyFeedList()
    }

    private fun getMyFeedList() {
        viewModelScope.launch {
            _getMyFeedListState.emit(UiState.Loading)

            feedRepository.getMyFeedList().cachedIn(viewModelScope)
                .catch { t ->
                    handleFailureState(_getMyFeedListState, t)
                }
                .collectLatest { pagingData ->
                    _getMyFeedListState.emit(UiState.Success(pagingData))
                }
        }
    }

    fun initGetMyFeedState() {
        _getMyFeedListState.value = UiState.Empty
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
            _postMyFeedLikeState.emit(UiState.Loading)

            feedRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { response ->
                    _postMyFeedLikeState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_postMyFeedLikeState, t) }
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            _deleteMyFeedState.emit(UiState.Loading)

            feedRepository.deleteFeed(feedId)
                .onSuccess { response ->
                    _deleteMyFeedState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_deleteMyFeedState, t) }
        }
    }

    fun initDeleteFeedState() {
        _deleteMyFeedState.value = UiState.Empty
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
