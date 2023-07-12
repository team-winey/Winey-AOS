package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityUploadBinding
import com.android.go.sopt.winey.util.binding.BindingActivity

class UploadActivity : BindingActivity<ActivityUploadBinding>(R.layout.activity_upload) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateTo<PhotoFragment>()
        initNextButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnUploadNext.setOnClickListener {
            when (supportFragmentManager.findFragmentById(R.id.fcv_upload)) {
                is PhotoFragment -> {
                    navigateTo<ContentFragment>()
                }

                is ContentFragment -> {
                    navigateTo<AmountFragment>()
                }
                is AmountFragment -> TODO("서버에 이미지 업로드 하면서 위니 피드로 돌아가기")
            }

        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }
}