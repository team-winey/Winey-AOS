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
    private var _userInfo = UserV2(
        nickname = "",
        userLevel = "",
        fcmIsAllowed = true,
        accumulatedAmount = 0,
        accumulatedCount = 0,
        amountSavedHundredDays = 0,
        amountSavedTwoWeeks = 0,
        amountSpentTwoWeeks = 0,
        remainingAmount = 0,
        remainingCount = 0
    )
    val userInfo get() = _userInfo

    init {
        initUserInfo()
    }

    private fun initUserInfo() {
        viewModelScope.launch {
            _userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
        }
    }
}
