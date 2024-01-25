package org.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityUploadBinding
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.main.feed.WineyFeedType
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.intent.getCompatibleSerializable

@AndroidEntryPoint
class UploadActivity : BindingActivity<ActivityUploadBinding>(R.layout.activity_upload) {
    private val feedType by lazy {
        intent.extras?.getCompatibleSerializable(WineyFeedFragment.KEY_FEED_TYPE)
            ?: WineyFeedType.SAVE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDefaultFragment(savedInstanceState)
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.fcv_upload,
                    PhotoFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable(WineyFeedFragment.KEY_FEED_TYPE, feedType)
                        }
                    }
                )
            }
        }
    }
}
