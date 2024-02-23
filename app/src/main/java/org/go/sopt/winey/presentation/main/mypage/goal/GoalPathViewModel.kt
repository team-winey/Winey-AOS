package org.go.sopt.winey.presentation.main.mypage.goal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.go.sopt.winey.domain.repository.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class GoalPathViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

}
