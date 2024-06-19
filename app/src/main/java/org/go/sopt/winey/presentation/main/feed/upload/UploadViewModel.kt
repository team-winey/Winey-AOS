package org.go.sopt.winey.presentation.main.feed.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import org.go.sopt.winey.domain.repository.FeedRepository
import org.go.sopt.winey.presentation.model.WineyFeedType
import org.go.sopt.winey.util.state.ErrorCode
import org.go.sopt.winey.util.multipart.UriToRequestBody
import org.go.sopt.winey.util.state.InputUiState
import org.go.sopt.winey.util.state.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    /** Common properties */
    lateinit var feedType: WineyFeedType

    /** PhotoFragment properties */
    private val _isImageSelected = MutableStateFlow(false)
    val isImageSelected: StateFlow<Boolean> = _isImageSelected.asStateFlow()
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    /** ContentFragment properties */
    val _content = MutableStateFlow("")
    val content: String get() = _content.value

    val inputUiState: StateFlow<InputUiState> = _content.map { checkInputUiState(it) }
        .stateIn(
            initialValue = InputUiState.Empty,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    val isValidContent: StateFlow<Boolean> = inputUiState.map { validateContent(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    /** AmountFragment properties */
    val _amount = MutableStateFlow("")
    val commaAmount: String get() = _amount.value

    val isValidAmount: StateFlow<Boolean> = _amount.map { validateAmount(it) }
        .stateIn(
            initialValue = false,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(PRODUCE_STOP_TIMEOUT)
        )

    /** Multipart properties */
    private var imageRequestBody: UriToRequestBody? = null
    private val _postWineyFeedState =
        MutableStateFlow<UiState<ResponsePostWineyFeedDto?>>(UiState.Empty)
    val postWineyFeedState: StateFlow<UiState<ResponsePostWineyFeedDto?>> =
        _postWineyFeedState.asStateFlow()

    /** Common functions */
    fun saveCurrentFeedType(feedType: WineyFeedType) {
        this.feedType = feedType
    }

    /** PhotoFragment functions */
    fun activateNextButton() {
        _isImageSelected.value = true
    }

    fun updateImageUri(imageUri: Uri) {
        _imageUri.value = imageUri
    }

    /** ContentFragment functions */
    private fun validateContent(state: InputUiState) = state == InputUiState.Success

    private fun checkInputUiState(content: String): InputUiState {
        if (content.isBlank()) return InputUiState.Empty
        if (!checkContentLength((content))) {
            return InputUiState.Failure(ErrorCode.CODE_INVALID_LENGTH)
        }
        return InputUiState.Success
    }

    private fun checkContentLength(content: String) =
        content.length in MIN_CONTENT_LENGTH..MAX_CONTENT_LENGTH

    /** AmountFragment functions */
    private fun validateAmount(amount: String): Boolean {
        if (amount.isBlank()) return false

        var temp = amount
        if (amount.contains(COMMA)) {
            temp = amount.removeComma()
        }
        return checkAmountRange(temp.toLong())
    }

    private fun checkAmountRange(amountNumber: Long) = amountNumber in MIN_AMOUNT..MAX_AMOUNT

    /** Multipart functions */
    fun updateRequestBody(requestBody: UriToRequestBody) {
        this.imageRequestBody = requestBody
    }

    fun postWineyFeed(content: String, amount: String, feedType: String) {
        if (!validateRequestBody()) return

        viewModelScope.launch {
            _postWineyFeedState.value = UiState.Loading

            val (file, requestMap) = createRequestBody(
                content = content,
                amount = amount,
                feedType = feedType
            )

            feedRepository.postWineyFeed(file, requestMap)
                .onSuccess { response ->
                    if (response == null) {
                        _postWineyFeedState.value = UiState.Failure("POST feed response is null")
                        return@launch
                    }

                    _postWineyFeedState.value = UiState.Success(response)
                }
                .onFailure { t ->
                    _postWineyFeedState.value = UiState.Failure(t.message.toString())

                    if (t is HttpException) {
                        Timber.e("${t.code()} ${t.message}")
                        return@onFailure
                    }
                    Timber.e(t.message)
                }
        }
    }

    private fun createRequestBody(
        content: String,
        amount: String,
        feedType: String
    ): Pair<MultipartBody.Part?, HashMap<String, RequestBody>> {
        val imageFormData = imageRequestBody?.toFormData()
        val contentBody = content.toPlainTextRequestBody()
        val amountBody = amount.toPlainTextRequestBody()
        val feedTypeBody = feedType.toPlainTextRequestBody()

        val plainTextRequestBodyMap = hashMapOf(
            KEY_FEED_TITLE to contentBody,
            KEY_FEED_MONEY to amountBody,
            KEY_FEED_TYPE to feedTypeBody
        )

        return Pair(imageFormData, plainTextRequestBodyMap)
    }

    fun initPostWineyFeedState() {
        _postWineyFeedState.value = UiState.Empty
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
        /** Content */
        private const val MIN_CONTENT_LENGTH = 6
        const val MAX_CONTENT_LENGTH = 100

        /** Amount */
        private const val MIN_AMOUNT = 1L
        private const val MAX_AMOUNT = 9999999L
        const val MAX_AMOUNT_LENGTH = 9

        /** Multipart */
        private const val KEY_FEED_TITLE = "feedTitle"
        private const val KEY_FEED_MONEY = "feedMoney"
        private const val KEY_FEED_TYPE = "feedType"

        private const val REQUEST_BODY_ERR_MSG = "Image RequestBody is null"
        private const val COMMA = ","
        private const val CONTENT_TYPE = "text/plain"
        private const val PRODUCE_STOP_TIMEOUT = 5000L
    }
}
