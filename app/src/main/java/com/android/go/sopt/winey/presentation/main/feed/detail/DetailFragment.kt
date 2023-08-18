package com.android.go.sopt.winey.presentation.main.feed.detail

import CommentAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentDetailBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class DetailFragment(private val feedId: Int) :
    BindingFragment<FragmentDetailBinding>(R.layout.fragment_detail) {
    private lateinit var commentAdapter: CommentAdapter
    private val viewModel by viewModels<DetailViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.getFeedDetail(feedId)
        initGetFeedDetailObserver()
    }

    private fun initAdapter() {
        commentAdapter = CommentAdapter()
        binding.rvDetailComment.adapter = commentAdapter
    }

    private fun initGetFeedDetailObserver() {
        viewModel.getFeedDetailState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    binding.data = state.data
                    commentAdapter.submitList(state.data?.commentList)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
    }

    companion object {
        private const val MSG_DETAIL_ERROR = "ERROR"
    }
}
