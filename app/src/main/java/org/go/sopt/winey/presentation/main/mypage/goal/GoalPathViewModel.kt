package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalPathViewModel : ViewModel() {
    private val _levelUpState = MutableStateFlow(false)
    val levelUpState: StateFlow<Boolean> = _levelUpState.asStateFlow()

    fun saveLevelUpState(currentState: Boolean) {
        _levelUpState.value = currentState
    }
}
