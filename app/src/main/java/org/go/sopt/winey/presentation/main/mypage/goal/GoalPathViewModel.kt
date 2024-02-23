package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class GoalPathViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

}
