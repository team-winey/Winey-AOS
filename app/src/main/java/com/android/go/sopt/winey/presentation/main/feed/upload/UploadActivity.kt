package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityUploadBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.photo.PhotoFragment
import com.android.go.sopt.winey.util.binding.BindingActivity

class UploadActivity : BindingActivity<ActivityUploadBinding>(R.layout.activity_upload) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateTo<PhotoFragment>()
//        initNextButtonClickListener()
//        initNavigationButtonClickListener()
    }

//    private fun initNextButtonClickListener() {
//        binding.btnUploadNext.setOnClickListener {
//            when (supportFragmentManager.findFragmentById(R.id.fcv_upload)) {
//                is PhotoFragment -> {
//                    navigateTo<ContentFragment>()
//                    setBackButton()
//                }
//
//                is ContentFragment -> {
//                    navigateTo<AmountFragment>()
//                    setBackButton()
//                    changeButtonText()
//                }
//
//                is AmountFragment -> {
//                    // todo: AmountFragment에 액티비티 뷰모델을 참조해서 함수로 최종 데이터를 저장하자!
//                    //  그러면 액티비티에서는 뷰모델의 서버통신 결과에 따라 UiState 처리
//                    //  로딩 상태일 때는 입력된 금액 범위에 따라 다이얼로그 띄우기 (시간 지연은 나중에 찾아보기)
//                }
//            }
//        }
//    }
//
//    private fun initNavigationButtonClickListener() {
//        binding.ivUploadNav.setOnClickListener {
//            when (supportFragmentManager.findFragmentById(R.id.fcv_upload)) {
//                is PhotoFragment -> {
//                    finish()
//                }
//
//                is ContentFragment -> {
//                    navigateTo<PhotoFragment>()
//                    setCloseButton()
//                }
//
//                is AmountFragment -> {
//                    navigateTo<ContentFragment>()
//                    setBackButton()
//                }
//            }
//        }
//    }
//
//    private fun setBackButton() {
//        binding.ivUploadNav.setImageDrawable(drawableOf(R.drawable.ic_upload_back))
//    }
//
//    private fun setCloseButton() {
//        binding.ivUploadNav.setImageDrawable(drawableOf(R.drawable.ic_upload_close))
//    }
//
//    private fun changeButtonText() {
//        binding.btnUploadNext.text = getString(R.string.upload_last_btn_text)
//    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_upload, T::class.simpleName)
        }
    }
}