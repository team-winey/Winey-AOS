package com.android.go.sopt.winey.presentation.main.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _getRecommendListState = MutableStateFlow<UiState<List<Recommend>>>(UiState.Loading)
    val getRecommendListState: StateFlow<UiState<List<Recommend>>> =
        _getRecommendListState.asStateFlow()

    init {
        getRecommendList()
    }

    fun getRecommendList() {
        viewModelScope.launch {
            authRepository.getRecommendList(1)
                .onSuccess { response ->
                    _getRecommendListState.value = UiState.Success(response)
                    Timber.e("성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("HTTP 실패")
                    }
                    Timber.e("${t.message}")
                    _getRecommendListState.value = UiState.Failure("${t.message}")
                }
        }
    }
}
