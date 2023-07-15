package com.android.go.sopt.winey.presentation.main.feed.upload.amount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentAmountBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class AmountFragment : BindingFragment<FragmentAmountBinding>(R.layout.fragment_amount) {
    private val viewModel by viewModels<AmountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel


    }
}