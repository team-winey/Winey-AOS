package com.android.go.sopt.winey.presentation.main.feed.detail

import CommentAdapter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityDetailBinding
import com.android.go.sopt.winey.domain.entity.Comment
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private val viewModel by viewModels<DetailViewModel>()

    private val feedId by lazy { intent.getIntExtra(KEY_FEED_ID, 0) }
    private val writerLevel by lazy { intent.getIntExtra(KEY_WRITER_LV, 0) }

    private var detailFeedAdapter: DetailFeedAdapter? = null
    private val commentEmptyAdapter by lazy { CommentEmptyAdapter() }
    private val commentAdapter by lazy { CommentAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        viewModel.getFeedDetail(feedId)
        initGetFeedDetailObserver()
        initBackButtonClickListener()
        initPostLikeStateObserver()
    }

    private fun initGetFeedDetailObserver() {
        viewModel.getFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val detailFeed = state.data
                    initDetailFeedAdapter(detailFeed)
                    switchCommentContainer(detailFeed?.commentList)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initDetailFeedAdapter(detailFeed: DetailFeed?) {
        if (detailFeed == null) {
            Timber.e("DETAIL FEED IS NULL")
            return
        }
        detailFeedAdapter =
            DetailFeedAdapter(
                detailFeed,
                postLike = { feedId, isLiked ->
                    viewModel.likeFeed(feedId, isLiked)
                }
            )
    }

    private fun switchCommentContainer(commentList: List<Comment>?) {
        if (commentList == null) {
            Timber.e("DETAIL COMMENT LIST IS NULL")
            return
        }

        if (commentList.isEmpty()) {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentEmptyAdapter)
        } else {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentAdapter)
            commentAdapter.submitList(commentList)
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postFeedDetailLikeState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    viewModel.getFeedDetail(feedId)

                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {

                }
            }
        }.launchIn(lifecycleScope)
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_WRITER_LV = "writerLevel"
    }
}
