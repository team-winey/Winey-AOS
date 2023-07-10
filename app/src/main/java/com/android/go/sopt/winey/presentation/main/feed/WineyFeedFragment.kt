package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class WineyFeedFragment : BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getFeed()
    }
    private fun initAdapter(){
        wineyFeedAdapter = WineyFeedAdapter()
        binding.rvWineyfeedPost.adapter = wineyFeedAdapter
    }

    private fun getFeed(){
        wineyFeedAdapter.submitList(viewModel.dummyFeedList)
    }

}