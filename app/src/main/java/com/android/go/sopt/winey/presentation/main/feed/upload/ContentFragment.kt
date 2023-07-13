package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentContentBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo: 에디트텍스트 내용이 채워지면 버튼 색상 변경 -> LiveData로 내용 감지
        // todo: 에디트텍스트의 길이 측정해서 카운터에 표시 -> LiveData로 길이 측정
        // todo: 36자까지만 작성하도록 제한
    }
}