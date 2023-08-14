package com.android.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityStoryBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.colorOf

class StoryActivity : BindingActivity<ActivityStoryBinding>(R.layout.activity_story) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateStatusBarColor()
        setUpDefaultFragment(savedInstanceState)
    }

    private fun updateStatusBarColor() {
        window.statusBarColor = colorOf(R.color.gray_50)
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            navigateTo<FirstStoryFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_story, T::class.simpleName)
        }
    }
}
