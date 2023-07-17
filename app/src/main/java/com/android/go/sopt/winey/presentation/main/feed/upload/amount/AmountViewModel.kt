package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.android.go.sopt.winey.util.context.UriToRequestBody
import timber.log.Timber

class AmountViewModel : ViewModel() {
    val _amount = MutableLiveData<String>()
    val amount: String get() = _amount.value ?: ""

    val isValidAmount: LiveData<Boolean> = _amount.map { validateLength(it) }

    private val _imageRequestBody = MutableLiveData<UriToRequestBody>()
    val imageRequestBody: LiveData<UriToRequestBody>
        get() = _imageRequestBody

    private fun validateLength(amount: String): Boolean =
        amount.length in MIN_AMOUNT_LENGTH..MAX_AMOUNT_LENGTH

    fun updateImageRequestBody(requestBody: UriToRequestBody) {
        _imageRequestBody.value = requestBody
    }

    fun postImage() {
        if(_imageRequestBody.value == null) {
            Timber.e("Image RequestBody is null")
        }else {
            // todo: 서버 통신 with Hilt
            Timber.e("서버 통신 시작 예정")
        }
    }

    companion object {
        const val MIN_AMOUNT_LENGTH = 1
        const val MAX_AMOUNT_LENGTH = 10
    }
}