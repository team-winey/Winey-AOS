package com.android.go.sopt.winey.presentation.main.feed.upload.content

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentContentBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class ContentFragment : BindingFragment<FragmentContentBinding>(R.layout.fragment_content) {
    private val viewModel by viewModels<ContentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel


    }
}