package com.android.go.sopt.winey.presentation.main.feed

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
class WineyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    lateinit var wineyFeedAdapter: WineyFeedAdapter

    private var currentPage = 0
    var isPagingFinished = false
    private var totalPage = Int.MAX_VALUE
    var currentMutableList = mutableListOf<WineyFeed>()

    private val _getWineyFeedListState =
        MutableStateFlow<UiState<PagingData<WineyFeed>>>(UiState.Loading)
    val getWineyFeedListState: StateFlow<UiState<PagingData<WineyFeed>>> =
        _getWineyFeedListState.asStateFlow()

    private val _postWineyFeedLikeState = MutableStateFlow<UiState<Like>>(UiState.Loading)
    val postWineyFeedLikeState: StateFlow<UiState<Like>>
        get() = _postWineyFeedLikeState

    val _deleteMyFeedState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val deleteMyFeedState: StateFlow<UiState<Unit>>
        get() = _deleteMyFeedState

    init {
        getWineyFeed()
    }

    fun likeFeed(feedId: Int, isLiked: Boolean) {
        val requestPostLikeDto = RequestPostLikeDto(isLiked)
        postLike(feedId, requestPostLikeDto)
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            authRepository.deleteFeed(feedId)
                .onSuccess { state ->
                    _deleteMyFeedState.value = UiState.Success(state)
                }
                .onFailure { t -> handleFailureState(_deleteMyFeedState, t) }
        }
    }

    private fun postLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto) {
        viewModelScope.launch {
            authRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { state ->
                    _postWineyFeedLikeState.value = UiState.Success(state)
                }
                .onFailure { t -> handleFailureState(_postWineyFeedLikeState, t) }
        }
    }

    fun getWineyFeed() {
        isPagingFinished = false
        if (currentPage > totalPage) {
            return
        }
        _getWineyFeedListState.value = UiState.Empty
        viewModelScope.launch {
            _getWineyFeedListState.value = UiState.Loading
//            authRepository.getWineyFeedList(++currentPage)
//                .onSuccess { state ->
//                    currentMutableList.addAll(state)
//                    if (state.isEmpty()) {
//                        totalPage = 0
//                        isPagingFinished = true
//                    } else {
//                        totalPage = currentMutableList[0].totalPageSize
//                    }
//                    val updatedList = currentMutableList.toList()
//                    _getWineyFeedListState.value = UiState.Success(updatedList)
//                }
//                .onFailure { t -> handleFailureState(_getWineyFeedListState, t) }

            val pagingData = authRepository.getWineyFeedList().cachedIn(viewModelScope)
                .collectLatest { data ->
                    _getWineyFeedListState.value = UiState.Success(data)
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
