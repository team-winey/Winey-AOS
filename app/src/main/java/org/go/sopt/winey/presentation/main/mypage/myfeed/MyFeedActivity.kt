package org.go.sopt.winey.presentation.main.mypage.myfeed

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentMyfeedBinding
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.presentation.main.feed.WineyFeedLoadAdapter
import org.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.context.wineySnackbar
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.WineyPopupMenu
import org.go.sopt.winey.util.view.snackbar.SnackbarType
import timber.log.Timber

@AndroidEntryPoint
class MyFeedActivity : BindingActivity<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedAdapter: MyFeedAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter
    private var clickedFeedId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        removeRecyclerviewItemChangeAnimation()
        initAdapter()
        initBackButtonClickListener()

        initGetMyFeedListStateObserver()
        initGetDetailFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
        initPagingLoadStateListener()
    }

    override fun onStart() {
        super.onStart()

        if (clickedFeedId != -1) {
            viewModel.getDetailFeed(clickedFeedId)
        }
    }

    private fun initGetDetailFeedStateObserver() {
        viewModel.getDetailFeedState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val detailFeed = state.data ?: return@onEach
                        myFeedAdapter.updateItem(clickedFeedId, detailFeed)
                        clickedFeedId = -1
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(lifecycleScope)
    }

    private fun initAdapter() {
        wineyFeedLoadAdapter = WineyFeedLoadAdapter()
        myFeedAdapter = MyFeedAdapter(
            onlikeButtonClicked = { wineyFeed ->
                viewModel.likeFeed(wineyFeed.feedId, !wineyFeed.isLiked)
            },
            onPopupMenuClicked = { anchorView, wineyFeed ->
                showFeedPopupMenu(anchorView, wineyFeed)
            },
            toFeedDetail = { wineyFeed ->
                navigateToDetail(wineyFeed)
                saveClickedFeedId(wineyFeed.feedId)
            }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
    }

    private fun saveClickedFeedId(feedId: Int) {
        clickedFeedId = feedId
    }

    private fun removeRecyclerviewItemChangeAnimation() {
        val animator = binding.rvMyfeedPost.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun showFeedPopupMenu(anchorView: View, wineyFeed: WineyFeed) {
        lifecycleScope.launch {
            showFeedDeletePopupMenu(anchorView, wineyFeed)
        }
    }

    private fun showFeedDeletePopupMenu(anchorView: View, wineyFeed: WineyFeed) {
        val deleteTitle = listOf(stringOf(R.string.popup_delete_title))
        WineyPopupMenu(
            context = anchorView.context,
            titles = deleteTitle
        ) { _, _, _ ->
            showFeedDeleteDialog(wineyFeed)
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showFeedDeleteDialog(feed: WineyFeed) {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.feed_delete_dialog_title),
                stringOf(R.string.feed_delete_dialog_subtitle),
                stringOf(R.string.comment_delete_dialog_negative_button),
                stringOf(R.string.comment_delete_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = {
                viewModel.deleteFeed(feed.feedId)
            }
        )
        dialog.show(supportFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun initBackButtonClickListener() {
        binding.imgMyfeedBack.setOnClickListener {
            finish()
        }
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteMyFeedState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val response = state.data ?: return@onEach
                    deletePagingDataItem(response.feedId.toInt())

                    wineySnackbar(
                        anchorView = binding.root,
                        message = stringOf(R.string.snackbar_feed_delete_success),
                        type = SnackbarType.WineyFeedResult(isSuccess = true)
                    )

                    // 상세피드 들어갔다 나올 때 성공 상태가 계속 관찰되어
                    // 스낵바가 반복해서 뜨지 않도록 UiState 초기화
                    viewModel.initDeleteFeedState()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun deletePagingDataItem(feedId: Int) {
        lifecycleScope.launch {
            val newList = myFeedAdapter.deleteItem(feedId)
            checkEmptyList(newList)
            myFeedAdapter.submitData(PagingData.from(newList))
        }
    }

    private fun checkEmptyList(newList: MutableList<WineyFeed>) {
        if (newList.isEmpty()) {
            binding.rvMyfeedPost.isVisible = false
            binding.clMyfeedEmpty.isVisible = true
        }
    }

    private fun initGetMyFeedListStateObserver() {
        viewModel.getMyFeedListState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val pagingData = state.data
                        myFeedAdapter.submitData(pagingData)

                        // 상세피드 들어갔다 나올 때, 성공 상태가 계속 관찰되어
                        // 이미 삭제된 항목이 되살아나는 일이 없도록 UiState 초기화
                        viewModel.initGetMyFeedState()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(lifecycleScope)
    }

    private fun initPagingLoadStateListener() {
        myFeedAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    Timber.d("LOADING")
                    binding.clMyfeedEmpty.isVisible = false
                    binding.rvMyfeedPost.isVisible = false
                }

                is LoadState.NotLoading -> {
                    Timber.d("NOT LOADING")
                    if (myFeedAdapter.itemCount > 0) {
                        binding.rvMyfeedPost.isVisible = true
                    } else {
                        binding.clMyfeedEmpty.isVisible = true
                    }
                }

                is LoadState.Error -> {
                    Timber.tag("failure").e(MSG_MYFEED_ERROR)
                }
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postMyFeedLikeState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    myFeedAdapter.updateLikeNumber(
                        state.data.data.feedId,
                        state.data.data.isLiked,
                        state.data.data.likes
                    )
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        Intent(this, DetailActivity::class.java).apply {
            putExtra(KEY_FEED_ID, wineyFeed.feedId)
            putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
            putExtra(KEY_PREV_SCREEN_NAME, MY_FEED_SCREEN)
            startActivity(this)
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"

        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val TAG_FEED_DELETE_DIALOG = "DELETE_DIALOG"
        private const val MY_FEED_SCREEN = "MyFeedFragment"
    }
}
