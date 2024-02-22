package org.go.sopt.winey.presentation.main.mypage.goal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityGoalPathBinding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.mypage.goal.model.UserLevel
import org.go.sopt.winey.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupGoalPathByUserLevel()
    }

    private fun setupGoalPathByUserLevel() {
        lifecycleScope.launch {
            val userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            when (userInfo.userLevel) {
                UserLevel.FIRST.rankName -> {
                    navigateTo<GoalPathLevel1Fragment>()
                }

                UserLevel.SECOND.rankName -> {
                    navigateTo<GoalPathLevel2Fragment>()
                }

                UserLevel.THIRD.rankName -> {
                    navigateTo<GoalPathLevel3Fragment>()
                }

                UserLevel.FORTH.rankName -> {
                    navigateTo<GoalPathLevel4Fragment>()
                }
            }
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_goal_path, T::class.simpleName)
        }
    }
}
