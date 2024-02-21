package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.entity.RemainingGoal
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.util.view.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoalPathViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _getGoalState = MutableStateFlow<UiState<RemainingGoal?>>(UiState.Empty)
    val getGoalState: StateFlow<UiState<RemainingGoal?>> = _getGoalState.asStateFlow()

    init {
        getRemainingGoal()
    }

    private fun getRemainingGoal() {
        viewModelScope.launch {
            authRepository.getRemainingGoal()
                .onSuccess { data ->
                    if (data == null) {
                        _getGoalState.emit(UiState.Failure("DATA IS NULL"))
                        Timber.e("FAIL GET REMAINING GOAL")
                        return@launch
                    }

                    _getGoalState.emit(UiState.Success(data))
                    Timber.d("SUCCESS GET REMAINING GOAL")
                }
                .onFailure { t ->
                    _getGoalState.emit(UiState.Failure(t.message.toString()))
                    Timber.e("FAIL GET REMAINING GOAL")
                }
        }
    }
}
