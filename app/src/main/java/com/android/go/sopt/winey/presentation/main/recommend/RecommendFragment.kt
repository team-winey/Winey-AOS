package com.android.go.sopt.winey.presentation.main.recommend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentRecommendBinding
import com.android.go.sopt.winey.presentation.main.feed.RecommendHeaderAdapter
import com.android.go.sopt.winey.util.binding.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendFragment : BindingFragment<FragmentRecommendBinding>(R.layout.fragment_recommend) {
    private val viewModel by viewModels<RecommendViewModel>()
    private lateinit var recommendAdapter: RecommendAdapter
    private lateinit var recommendHeaderAdapter: RecommendHeaderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendAdapter = RecommendAdapter()
        recommendAdapter.submitList(viewModel.recommendList)

        recommendHeaderAdapter = RecommendHeaderAdapter()
        binding.rvRecommendPost.adapter = ConcatAdapter(recommendHeaderAdapter, recommendAdapter)
    }
}