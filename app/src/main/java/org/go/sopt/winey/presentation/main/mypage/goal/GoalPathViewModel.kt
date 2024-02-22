package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.entity.RemainingGoal
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.util.view.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoalPathViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

}
