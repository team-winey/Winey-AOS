package org.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityUploadBinding
import org.go.sopt.winey.util.binding.BindingActivity

@AndroidEntryPoint
class UploadActivity : BindingActivity<ActivityUploadBinding>(R.layout.activity_upload) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDefaultFragment(savedInstanceState)
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcv_upload, PhotoFragment())
            }
        }
    }
}
