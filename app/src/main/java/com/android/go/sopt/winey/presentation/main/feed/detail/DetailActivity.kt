package com.android.go.sopt.winey.presentation.main.feed.detail

import android.os.Bundle
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityDetailBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private lateinit var detailFragment: DetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val feedId = intent.getIntExtra("feedId", 0)
        val writerLevel = intent.getIntExtra("writerLevel", 0)
        putArgsToDetail(feedId, writerLevel)
        setDefaultFragment(savedInstanceState)
    }

    private fun putArgsToDetail(feedId: Int, writerLevel: Int) {
        detailFragment = DetailFragment()
        detailFragment.arguments = Bundle().apply {
            putInt("feedId", feedId)
            putInt("writerLevel", writerLevel)
        }
    }

    private fun setDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcv_detail, detailFragment)
            }
        }
    }
}
