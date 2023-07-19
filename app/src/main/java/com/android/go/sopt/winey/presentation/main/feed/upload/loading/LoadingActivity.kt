package com.android.go.sopt.winey.presentation.main.feed.upload.loading

import android.os.Bundle
import android.os.Handler
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityLoadingBinding
import com.android.go.sopt.winey.util.binding.BindingActivity

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            finish()
        }, DELAY_TIME)
    }

    companion object {
        private const val DELAY_TIME = 3000L
    }
}