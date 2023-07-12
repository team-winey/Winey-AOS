package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedAdapter: MyFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getFeed()
    }

    private fun initAdapter() {
        myFeedAdapter = MyFeedAdapter()
        binding.rvMyfeedPost.adapter = myFeedAdapter
    }

    private fun getFeed() {
        myFeedAdapter.submitList(viewModel.dummyFeedList)
    }
}
