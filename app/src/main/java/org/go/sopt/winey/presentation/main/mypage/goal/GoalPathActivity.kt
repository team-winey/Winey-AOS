package org.go.sopt.winey.presentation.main.mypage.goal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityGoalPathBinding
import org.go.sopt.winey.util.binding.BindingActivity

class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDefaultFragment(savedInstanceState)
    }

    private fun setupDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            navigateTo<GoalPathLevel1Fragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
    }
}
