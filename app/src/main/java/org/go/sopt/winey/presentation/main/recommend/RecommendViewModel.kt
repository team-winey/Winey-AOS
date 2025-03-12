package org.go.sopt.winey.presentation.main.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.entity.Recommend
import org.go.sopt.winey.domain.repository.RecommendRepository
import org.go.sopt.winey.util.state.UiState
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val recommendRepository: RecommendRepository
) : ViewModel() {
    private val _getRecommendListState =
        MutableStateFlow<UiState<List<Recommend>?>>(UiState.Loading)
    val getRecommendListState: StateFlow<UiState<List<Recommend>?>> =
        _getRecommendListState.asStateFlow()

    init {
        getRecommendList()
    }

    fun getRecommendList() {
        viewModelScope.launch {
            recommendRepository.getRecommendList(1)
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
