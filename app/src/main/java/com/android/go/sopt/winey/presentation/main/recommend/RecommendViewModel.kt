package com.android.go.sopt.winey.presentation.main.recommend

import android.util.Log
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
                    Log.e("test log", "성공")
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Log.e("test log", "HTTP 실패")
                    }
                    Log.e("test log", "${t.message}")
                    _getRecommendListState.value = UiState.Failure("${t.message}")
                }
        }
    }
}
