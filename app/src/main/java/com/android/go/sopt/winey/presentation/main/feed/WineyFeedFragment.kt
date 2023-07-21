package com.android.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.data.interceptor.AuthInterceptor
import com.android.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.presentation.main.MainViewModel
import com.android.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WineyFeedFragment : BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var wineyFeedDialogFragment: WineyFeedDialogFragment
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private var totalPage = Int.MAX_VALUE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListWithInfiniteScroll()
        initFabClickListener()
        initPostLikeStateObserver()
        initGetFeedStateObserver()
    }

    private fun initAdapter() {
        wineyFeedAdapter = WineyFeedAdapter(
            likeButtonClick = { feedId, isLiked ->
                viewModel.likeFeed(feedId, isLiked)
            },
            showPopupMenu = { view, wineyFeed ->
                showPopupMenu(view, wineyFeed)
            }
        )
        viewModel.wineyFeedAdapter = wineyFeedAdapter
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter()
        binding.rvWineyfeedPost.adapter = ConcatAdapter(wineyFeedHeaderAdapter, wineyFeedAdapter)
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_wineyfeed, popupMenu.menu)
        val menuDelete = popupMenu.menu.findItem(R.id.menu_delete)
        val menuReport = popupMenu.menu.findItem(R.id.menu_report)
        if (wineyFeed.userId == AuthInterceptor.USER_ID.toInt()) {
            menuReport.isVisible = false
        } else menuDelete.isVisible = false
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    if (wineyFeed.writerLevel <= 2) {
                        showLowDeleteDialog(wineyFeed.feedId)
                    } else {
                        showHighDeleteDialog(wineyFeed.feedId)
                    }
                    true
                }

                else -> false
                /* 신고 구현 : 앱잼 내에서는 없음 */
            }
        }
        popupMenu.show()
    }

    private fun showLowDeleteDialog(feedId: Int) {
        val wineyFeedLowDeleteDialogFragment =
            WineyFeedLowDeleteDialogFragment(feedId)
        wineyFeedLowDeleteDialogFragment.show(parentFragmentManager, "delete")
    }

    private fun showHighDeleteDialog(feedId: Int) {
        val wineyFeedHighDeleteDialogFragment =
            WineyFeedHighDeleteDialogFragment(feedId)
        wineyFeedHighDeleteDialogFragment.show(parentFragmentManager, "delete")
    }

    private fun initGetFeedStateObserver() {
        viewModel.getWineyFeedListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    val wineyFeedList = state.data
                    wineyFeedAdapter.submitList(wineyFeedList)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postWineyFeedLikeState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    initGetFeedStateObserver()
                    wineyFeedAdapter.updateLikeStatus(state.data.data.feedId, state.data.data.isLiked)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
            }
        }
    }

    private fun initFabClickListener() {
        binding.btnWineyfeedFloating.setOnSingleClickListener {
            initGetUserStateObserver()
        }
    }

    private fun initGetUserStateObserver() {
        mainViewModel.getUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    isGoalValid(state.data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
            }
        }
    }

    private fun isGoalValid(data: User) {
        if (data.isOver) {
            wineyFeedDialogFragment = WineyFeedDialogFragment()
            wineyFeedDialogFragment.show(parentFragmentManager, TAG_WINEYFEED_DIALOG)
        } else navigateToUpload()
    }

    private fun setListWithInfiniteScroll() {
        binding.rvWineyfeedPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    var itemCount = wineyFeedAdapter.itemCount
                    var lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (binding.rvWineyfeedPost.canScrollVertically(1) && lastVisibleItemPosition == itemCount) {
                        lastVisibleItemPosition += MAX_FEED_VER_PAGE
                        itemCount += MAX_FEED_VER_PAGE
                        viewModel.getWineyFeed()
                        initGetFeedStateObserver()
                    }
                }
            }
        })
    }

    private fun navigateToUpload() {
        val intent = Intent(requireContext(), UploadActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
        private const val MSG_WINEYFEED_ERROR = "ERROR"
        private const val MAX_FEED_VER_PAGE = 10
    }
}