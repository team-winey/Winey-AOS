package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.UriToRequestBody
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AmountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: String get() = _amount.value ?: ""

    val isValidAmount: LiveData<Boolean> = _amount.map { validateAmount(it.removeComma()) }

    private val _imageRequestBody = MutableLiveData<UriToRequestBody>()
    val imageRequestBody: LiveData<UriToRequestBody>
        get() = _imageRequestBody

    private val _postWineyFeedState = MutableLiveData<UiState<ResponsePostWineyFeedDto?>>()
    val postWineyFeedState: LiveData<UiState<ResponsePostWineyFeedDto?>>
        get() = _postWineyFeedState

    private fun String.removeComma(): Long = replace(",", "").toLong()

    private fun validateAmount(amount: Long): Boolean = amount in MIN_AMOUNT..MAX_AMOUNT

    fun updateRequestBody(requestBody: UriToRequestBody) {
        _imageRequestBody.value = requestBody
    }

    fun postWineyFeed(content: String, amount: String) {
        if (_imageRequestBody.value == null) {
            Timber.e("Image RequestBody is null")
            return
        }

        viewModelScope.launch {
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val amountBody = amount.toRequestBody("text/plain".toMediaTypeOrNull())
            val stringRequestBodyMap = hashMapOf(
                FEED_TITLE_KEY to contentBody,
                FEED_MONEY_KEY to amountBody
            )
            val imageRequestBody = imageRequestBody.value?.toFormData()

            authRepository.postWineyFeed(imageRequestBody, stringRequestBodyMap)
                .onSuccess { response ->
                    _postWineyFeedState.value = UiState.Success(response)
                    Timber.d("${response?.feedId} ${response?.createdAt}")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        _postWineyFeedState.value = UiState.Failure(t.message())
                        Timber.e("${t.code()} ${t.message()}")
                    }

                    _postWineyFeedState.value = UiState.Failure(t.message.toString())
                    Timber.e(t)
                }
        }
    }

    companion object {
        const val MIN_AMOUNT = 1
        const val MAX_AMOUNT = 9999999
        private const val FEED_TITLE_KEY = "feedTitle"
        private const val FEED_MONEY_KEY = "feedMoney"
    }
}