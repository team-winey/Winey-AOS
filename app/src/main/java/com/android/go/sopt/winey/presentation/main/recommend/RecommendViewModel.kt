package com.android.go.sopt.winey.presentation.main.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): ViewModel() {
    val recommendList = listOf(
        Recommend(
            id = 1,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
        Recommend(
            id = 2,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
        Recommend(
            id = 3,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
        Recommend(
            id = 4,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
        Recommend(
            id = 5,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
        Recommend(
            id = 6,
            link = "asd",
            title = "asdf",
            subtitle = "asdasd",
            discount = "asdasdasd",
            image = "asdasdasd",
        ),
    )

    init {
        getRecommendList()
    }

    private val _getRecommendListState = MutableLiveData<UiState<List<Recommend>>>(UiState.Loading)
    val getRecommendListState : LiveData<UiState<List<Recommend>>> = _getRecommendListState

    fun getRecommendList(){
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