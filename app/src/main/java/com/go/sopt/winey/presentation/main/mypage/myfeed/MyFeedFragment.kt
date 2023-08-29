package com.go.sopt.winey.presentation.main.mypage.myfeed

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.go.sopt.winey.domain.entity.WineyFeed
import com.go.sopt.winey.presentation.main.feed.WineyFeedLoadAdapter
import com.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import com.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.go.sopt.winey.util.binding.BindingFragment
import com.go.sopt.winey.util.fragment.WineyDialogFragment
import com.go.sopt.winey.util.fragment.snackBar
import com.go.sopt.winey.util.fragment.stringOf
import com.go.sopt.winey.util.fragment.viewLifeCycle
import com.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.go.sopt.winey.util.fragment.wineySnackbar
import com.go.sopt.winey.util.view.UiState
import com.go.sopt.winey.util.view.WineyPopupMenu
import com.go.sopt.winey.util.view.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private var selectedScrollPosition: Parcelable? = null
    private var selectedItemIndex: Int = -1
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedAdapter: MyFeedAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        removeRecyclerviewItemChangeAnimation()
        initAdapter()
        initBackButtonClickListener()

        initGetFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getMyFeed()
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
            toFeedDetail = { wineyFeed -> navigateToDetail(wineyFeed) }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
    }

    private fun restoreScrollPosition() {
        binding.rvMyfeedPost.post {
            if (selectedItemIndex != -1) {
                binding.rvMyfeedPost.layoutManager?.scrollToPosition(selectedItemIndex + 1)
            }
        }
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
        WineyPopupMenu(context = anchorView.context, titles = deleteTitle) { _, _, _ ->
            showFeedDeleteDialog(wineyFeed)
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun WineyPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_OFFSET, -POPUP_MENU_OFFSET, Gravity.END)
    }

    private fun showFeedDeleteDialog(feed: WineyFeed) {
        val dialog = WineyDialogFragment(
            stringOf(R.string.feed_delete_dialog_title),
            stringOf(R.string.feed_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = {
                viewModel.deleteFeed(feed.feedId)
                deletePagingDataItem(feed.feedId)
            }
        )
        dialog.show(parentFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun deletePagingDataItem(feedId: Int) {
        viewLifeCycleScope.launch {
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

    private fun initBackButtonClickListener() {
        binding.imgMyfeedBack.setOnSingleClickListener {
            navigateTo<MyPageFragment>()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteMyFeedState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    wineySnackbar(requireView(), true, stringOf(R.string.snackbar_feed_delete_success))
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
    }

    private fun initGetFeedStateObserver() {
        viewLifeCycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMyFeedListState.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            myFeedAdapter.addLoadStateListener { loadState ->
                                when (loadState.refresh) {
                                    is LoadState.Loading -> {
                                        binding.clMyfeedEmpty.isVisible = false
                                        binding.rvMyfeedPost.isVisible = false
                                    }

                                    is LoadState.NotLoading -> {
                                        binding.rvMyfeedPost.isVisible = myFeedAdapter.itemCount > 0
                                        binding.clMyfeedEmpty.isVisible = myFeedAdapter.itemCount == 0
                                        restoreScrollPosition()
                                    }

                                    is LoadState.Error -> {
                                        Timber.tag("failure").e(MSG_MYFEED_ERROR)
                                    }
                                }
                            }
                            myFeedAdapter.submitData(state.data)
                        }

                        is UiState.Failure -> {
                            snackBar(binding.root) { state.msg }
                        }

                        else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
                    }
                }
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postMyFeedLikeState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    myFeedAdapter.updateItem(
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
        }.launchIn(viewLifeCycleScope)
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        selectedItemIndex = myFeedAdapter.snapshot().indexOf(wineyFeed)
        selectedScrollPosition = binding.rvMyfeedPost.layoutManager?.onSaveInstanceState()
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(KEY_FEED_ID, wineyFeed.feedId)
        intent.putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
        startActivity(intent)
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bnv_main)
        bottomNav.selectedItemId = R.id.menu_mypage
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val POPUP_MENU_OFFSET = 65
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val TAG_FEED_DELETE_DIALOG = "DELETE_DIALOG"
    }
}
