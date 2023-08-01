package com.android.go.sopt.winey.presentation.main.recommend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentRecommendBinding
import com.android.go.sopt.winey.presentation.main.feed.RecommendHeaderAdapter
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendFragment : BindingFragment<FragmentRecommendBinding>(R.layout.fragment_recommend) {
    private val viewModel by viewModels<RecommendViewModel>()
    private lateinit var recommendAdapter: RecommendAdapter
    private lateinit var recommendHeaderAdapter: RecommendHeaderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        getRecommendListStateObserver()
    }

    private fun initAdapter() {
        recommendAdapter = RecommendAdapter()
        recommendHeaderAdapter = RecommendHeaderAdapter()
        binding.rvRecommendPost.adapter =
            ConcatAdapter(recommendHeaderAdapter, recommendAdapter)
    }

    private fun getRecommendListStateObserver() {
        viewModel.getRecommendListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    recommendAdapter.submitList(state.data)
                }

                is UiState.Failure -> {
                }

                is UiState.Empty -> {
                }
            }
        }
    }
}
