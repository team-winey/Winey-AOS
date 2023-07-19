package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.util.Log
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
class MyFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _getMyFeedListState = MutableLiveData<UiState<List<WineyFeed>>>(UiState.Loading)
    val getMyFeedListState: LiveData<UiState<List<WineyFeed>>>
        get() = _getMyFeedListState

    private val _postMyFeedLikeState = MutableLiveData<UiState<Like>>(UiState.Loading)
    val postMyFeedLikeState: LiveData<UiState<Like>>
        get() = _postMyFeedLikeState

    val _deleteMyFeedState = MutableLiveData<UiState<Unit>>(UiState.Loading)
    val deleteMyFeedState: LiveData<UiState<Unit>>
        get() = _deleteMyFeedState

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
                .onSuccess { state ->
                    _postMyFeedLikeState.value = UiState.Success(state)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _postMyFeedLikeState.value.apply {
                            when (t.code()) {
                                CODE_MYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_MYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else -> UiState.Failure(t.message())
                            }
                        }
                        Timber.e("$MSG_MYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    fun getMyFeed() {
        viewModelScope.launch {
            authRepository.getMyFeedList(MYFEED_FEED_PAGE)
                .onSuccess { response ->
                    _getMyFeedListState.value = UiState.Success(response)
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
                        Timber.e("$MSG_MYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            authRepository.deleteFeed(feedId)
                .onSuccess { state ->
                    _deleteMyFeedState.value = UiState.Success(state)
                    Log.e("deleteSuccess", state.toString())
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Log.e("deleteFail", t.message())
                        _deleteMyFeedState.value.apply {
                            when (t.code()) {
                                CODE_MYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_MYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else -> _deleteMyFeedState.value =
                                    UiState.Failure(t.message())
                            }
                        }
                        Timber.e("${MSG_MYFEED_FAIL} : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }


    companion object {
        private const val CODE_MYFEED_INVALID_USER = 404
        private const val CODE_MYFEED_INVALID_REQUEST = 400
        private const val MSG_MYFEED_FAIL = "FAIL"
        private const val MYFEED_FEED_PAGE = 1
    }
}