package com.android.go.sopt.winey.presentation.main.recommend

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentRecommendBinding
import com.android.go.sopt.winey.presentation.main.MainViewModel
import com.android.go.sopt.winey.presentation.main.feed.RecommendHeaderAdapter
import com.android.go.sopt.winey.presentation.main.notification.NotificationActivity
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RecommendFragment : BindingFragment<FragmentRecommendBinding>(R.layout.fragment_recommend) {
    private val viewModel by viewModels<RecommendViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var recommendAdapter: RecommendAdapter
    private lateinit var recommendHeaderAdapter: RecommendHeaderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        getRecommendListStateObserver()
        binding.vm = mainViewModel
        mainViewModel.getHasNewNoti()
        initNotificationButtonClickListener()
    }

    private fun initAdapter() {
        recommendAdapter = RecommendAdapter()
        recommendHeaderAdapter = RecommendHeaderAdapter()
        binding.rvRecommendPost.adapter =
            ConcatAdapter(recommendHeaderAdapter, recommendAdapter)
    }

    private fun getRecommendListStateObserver() {
        viewModel.getRecommendListState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    recommendAdapter.submitList(state.data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initNotificationButtonClickListener() {
        binding.ivRecommendNotification.setOnClickListener {
            mainViewModel.patchCheckAllNoti()
            val intent = Intent(context, NotificationActivity::class.java)
            startActivity(intent)
        }
    }
}
