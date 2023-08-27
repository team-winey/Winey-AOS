package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityUploadBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

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
