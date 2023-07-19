package com.android.go.sopt.winey.presentation.main.feed

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.domain.entity.Like
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
    var wineyFeedAdapter: WineyFeedAdapter

    private val _getWineyFeedListState = MutableLiveData<UiState<List<WineyFeed>>>(UiState.Loading)
    val getWineyFeedListState: LiveData<UiState<List<WineyFeed>>>
        get() = _getWineyFeedListState

    private val _postWineyFeedLikeState = MutableLiveData<UiState<Like>>(UiState.Loading)
    val postWineyFeedLikeState: LiveData<UiState<Like>>
        get() = _postWineyFeedLikeState

    init {
        getWineyFeed()
        wineyFeedAdapter = WineyFeedAdapter(
            likeButtonClick = { feedId, isLiked ->
                postLike(feedId, RequestPostLikeDto(isLiked))
            },
            showPopupMenu = { view, wineyFeed ->
                showPopupMenu(view, wineyFeed)
            }
        )
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {

    }


    private fun postLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto) {
        viewModelScope.launch {
            authRepository.postFeedLike(feedId, requestPostLikeDto)
                .onSuccess { state ->
                    _postWineyFeedLikeState.value = UiState.Success(state)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _postWineyFeedLikeState.value.apply {
                            when (t.code()) {
                                CODE_WINEYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_WINEYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else -> UiState.Failure(t.message())
                            }
                        }
                        Timber.e("$MSG_WINEYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    fun getWineyFeed() {
        viewModelScope.launch {
            authRepository.getWineyFeedList(WINEY_FEED_PAGE)
                .onSuccess { state ->
                    _getWineyFeedListState.value = UiState.Success(state)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _getWineyFeedListState.value.apply {
                            when (t.code()) {
                                CODE_WINEYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_WINEYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else -> UiState.Failure(t.message())
                            }
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
        private const val WINEY_FEED_PAGE = 1
    }
}