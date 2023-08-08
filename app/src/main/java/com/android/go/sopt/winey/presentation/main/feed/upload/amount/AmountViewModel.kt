package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.multipart.UriToRequestBody
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AmountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _amount = MutableStateFlow("")
    val amount: String get() = _amount.value

    val isValidAmount: StateFlow<Boolean> = _amount.map { validateAmount(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    private var imageRequestBody: UriToRequestBody? = null

    private val _postWineyFeedState =
        MutableStateFlow<UiState<ResponsePostWineyFeedDto?>>(UiState.Loading)
    val postWineyFeedState: StateFlow<UiState<ResponsePostWineyFeedDto?>> =
        _postWineyFeedState.asStateFlow()

    private fun validateAmount(amount: String): Boolean {
        if (amount.isBlank()) return false

        var temp = amount
        if (amount.contains(COMMA)) {
            temp = amount.removeComma()
        }
        return checkAmountRange(temp.toLong())
    }

    private fun checkAmountRange(amountNumber: Long) = amountNumber in MIN_AMOUNT..MAX_AMOUNT

    fun updateRequestBody(requestBody: UriToRequestBody) {
        this.imageRequestBody = requestBody
    }

    fun postWineyFeed(content: String, amount: String) {
        if (!validateRequestBody()) return

        viewModelScope.launch {
            val (file, requestMap) = createRequestBody(content, amount)
            authRepository.postWineyFeed(file, requestMap)
                .onSuccess { response ->
                    _postWineyFeedState.value = UiState.Success(response)
                    Timber.d("${response?.feedId} ${response?.createdAt}")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _postWineyFeedState.value = UiState.Failure(t.message())
                        Timber.e("${t.code()} ${t.message()}")
                        return@onFailure
                    }
                    _postWineyFeedState.value = UiState.Failure(t.message.toString())
                    Timber.e(t.message)
                }
        }
    }

    private fun createRequestBody(
        content: String,
        amount: String
    ): Pair<MultipartBody.Part?, HashMap<String, RequestBody>> {
        val imageFormData = imageRequestBody?.toFormData()
        val contentBody = content.toPlainTextRequestBody()
        val amountBody = amount.toPlainTextRequestBody()
        val plainTextRequestBodyMap = hashMapOf(
            FEED_TITLE_KEY to contentBody,
            FEED_MONEY_KEY to amountBody
        )
        return Pair(imageFormData, plainTextRequestBodyMap)
    }

    private fun validateRequestBody(): Boolean {
        if (imageRequestBody == null) {
            _postWineyFeedState.value = UiState.Failure(REQUEST_BODY_ERR_MSG)
            Timber.e(REQUEST_BODY_ERR_MSG)
            return false
        }
        return true
    }

    private fun String.removeComma() = replace(COMMA, "")

    private fun String.toPlainTextRequestBody() = toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())

    companion object {
        const val MAX_AMOUNT_LENGTH = 9
        private const val MIN_AMOUNT = 1L
        private const val MAX_AMOUNT = 9999999L
        private const val FEED_TITLE_KEY = "feedTitle"
        private const val FEED_MONEY_KEY = "feedMoney"
        private const val REQUEST_BODY_ERR_MSG = "Image RequestBody is null"
        private const val COMMA = ","
        private const val CONTENT_TYPE = "text/plain"
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
