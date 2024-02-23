package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.go.sopt.winey.domain.repository.DataStoreRepository
import javax.inject.Inject

class GoalPathViewModel : ViewModel() {
    private val _levelUpState = MutableStateFlow(false)
    val levelUpState: StateFlow<Boolean> = _levelUpState.asStateFlow()

    fun initLevelUpState(currentState: Boolean) {
        _levelUpState.value = currentState
    }
}
