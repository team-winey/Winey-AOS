package com.android.go.sopt.winey.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityMainBinding
import com.android.go.sopt.winey.presentation.main.feed.FeedFragment
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.presentation.main.recommend.RecommendFragment
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeFragment(FeedFragment())

        binding.bnvMainView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_feed -> changeFragment(FeedFragment())
                R.id.menu_recommend -> changeFragment(RecommendFragment())
                R.id.menu_mypage -> changeFragment(MyPageFragment())
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main_view, fragment)
            .commit()
    }
}