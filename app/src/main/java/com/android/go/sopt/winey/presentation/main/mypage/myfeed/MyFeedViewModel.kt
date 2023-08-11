package com.android.go.sopt.winey.presentation.main.mypage.myfeed

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

    private var currentPage = 0
    private var isPagingFinished = false
    private var totalPage = Int.MAX_VALUE
    var currentMutableList = mutableListOf<WineyFeed>()

    private val _getMyFeedListState = MutableLiveData<UiState<List<WineyFeed>>>(UiState.Loading)
    val getMyFeedListState: LiveData<UiState<List<WineyFeed>>>
        get() = _getMyFeedListState

    private val _postMyFeedLikeState = MutableLiveData<UiState<Like>>(UiState.Loading)
    val postMyFeedLikeState: LiveData<UiState<Like>>
        get() = _postMyFeedLikeState

    val _deleteMyFeedState = MutableLiveData<UiState<Unit>>(UiState.Loading)
    val deleteMyFeedState: LiveData<UiState<Unit>>
        get() = _deleteMyFeedState

//    init {
//        getMyFeed()
//    }

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

//    fun getMyFeed() {
//        isPagingFinished = false
//        if (isPagingFinished || currentPage > totalPage) {
//            return
//        } else {
//            viewModelScope.launch {
//                authRepository.getMyFeedList(++currentPage)
//                    .onSuccess { state ->
//                        currentMutableList.addAll(state)
//                        if (state.isEmpty()) {
//                            totalPage = 0
//                            isPagingFinished = true
//                        } else {
//                            totalPage = currentMutableList[0].totalPageSize
//                        }
//                        val updatedList = currentMutableList.toList()
//                        _getMyFeedListState.value = UiState.Success(updatedList)
//                    }
//                    .onFailure { t ->
//                        if (t is HttpException) {
//                            when (t.code()) {
//                                CODE_MYFEED_INVALID_USER ->
//                                    _getMyFeedListState.value =
//                                        UiState.Failure(t.message())
//
//                                CODE_MYFEED_INVALID_REQUEST ->
//                                    _getMyFeedListState.value =
//                                        UiState.Failure(t.message())
//
//                                else ->
//                                    _getMyFeedListState.value =
//                                        UiState.Failure(t.message())
//                            }
//                            Timber.e("$MSG_MYFEED_FAIL : ${t.code()} : ${t.message()}")
//                        }
//                    }
//            }
//        }
//    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            authRepository.deleteFeed(feedId)
                .onSuccess { state ->
                    _deleteMyFeedState.value = UiState.Success(state)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _deleteMyFeedState.value.apply {
                            when (t.code()) {
                                CODE_MYFEED_INVALID_USER ->
                                    UiState.Failure(t.message())

                                CODE_MYFEED_INVALID_REQUEST ->
                                    UiState.Failure(t.message())

                                else ->
                                    _deleteMyFeedState.value =
                                        UiState.Failure(t.message())
                            }
                        }
                        Timber.e("$MSG_MYFEED_FAIL : ${t.code()} : ${t.message()}")
                    }
                }
        }
    }

    companion object {
        private const val CODE_MYFEED_INVALID_USER = 404
        private const val CODE_MYFEED_INVALID_REQUEST = 400
        private const val MSG_MYFEED_FAIL = "FAIL"
    }
}
