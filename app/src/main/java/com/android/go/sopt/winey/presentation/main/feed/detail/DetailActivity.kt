package com.android.go.sopt.winey.presentation.main.feed.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityDetailBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private val viewModel by viewModels<DetailViewModel>()
    private lateinit var detailFragment: DetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        putArgsToDetailFragment()
        setDefaultFragment(savedInstanceState)
    }

    private fun putArgsToDetailFragment() {
        detailFragment = DetailFragment()

        val feedId = intent.getIntExtra(EXTRA_KEY_FEED_ID, 0)
        val writerLevel = intent.getIntExtra(EXTRA_KEY_WRITER_LV, 0)

        detailFragment.arguments = Bundle().apply {
            putInt(EXTRA_KEY_FEED_ID, feedId)
            putInt(EXTRA_KEY_WRITER_LV, writerLevel)
        }
    }

    private fun setDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcv_detail, detailFragment)
            }
        }
    }

    companion object {
        private const val EXTRA_KEY_FEED_ID = "feedId"
        private const val EXTRA_KEY_WRITER_LV = "writerLevel"
    }
}
