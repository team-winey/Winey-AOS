package com.android.go.sopt.winey.presentation.main.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.domain.repository.FeedRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    val _comment = MutableStateFlow("")
    val comment: String = _comment.value

    val isValidComment: StateFlow<Boolean> = _comment.map { validateComment(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    val isLongText: StateFlow<Boolean> = _comment.map { checkLength(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private fun validateComment(comment: String): Boolean =
        comment.isNotBlank() && comment.length in MIN_COMMENT_LENGTH..MAX_COMMENT_LENGTH

    private fun checkLength(comment: String) =
        comment.isNotBlank() && comment.length >= LONG_TEXT_LENGTH

    private val _getFeedDetailState =
        MutableStateFlow<UiState<DetailFeed?>>(UiState.Loading)
    val getFeedDetailState: StateFlow<UiState<DetailFeed?>> =
        _getFeedDetailState.asStateFlow()

    fun getFeedDetail(feedId: Int) {
        viewModelScope.launch {
            feedRepository.getFeedDetail(feedId)
                .onSuccess { response ->
                    _getFeedDetailState.emit(UiState.Success(response))
                }
                .onFailure { t -> handleFailureState(_getFeedDetailState, t) }
        }
    }

    private fun <T> handleFailureState(loadingState: MutableStateFlow<UiState<T>>, t: Throwable) {
        if (t is HttpException) {
            val errorMessage = when (t.code()) {
                CODE_DETAIL_INVALID_USER_OR_FEED -> t.message()
                CODE_DETAIL_INVALID_REQUEST -> t.message()
                else -> t.message()
            }
            loadingState.value = UiState.Failure(errorMessage)
            Timber.e("$MSG_DETAIL_FAIL : ${t.code()} : ${t.message()}")
        }
    }

    companion object {
        private const val CODE_DETAIL_INVALID_USER_OR_FEED = 404
        private const val CODE_DETAIL_INVALID_REQUEST = 400
        private const val MSG_DETAIL_FAIL = "FAIL"

        private const val PRODUCE_STOP_TIMEOUT = 5000L
        private const val MIN_COMMENT_LENGTH = 1
        private const val LONG_TEXT_LENGTH = 450
        const val MAX_COMMENT_LENGTH = 500
    }
}