package org.go.sopt.winey.presentation.main.mypage.myfeed

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
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
import org.go.sopt.winey.presentation.main.mypage.MyPageFragment
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.fragment.wineySnackbar
import org.go.sopt.winey.util.view.snackbar.SnackbarType
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.WineyPopupMenu
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedAdapter: MyFeedAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter
    private var clickedFeedId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            Timber.d("onStart getDetailFeed")
            viewModel.getDetailFeed(clickedFeedId)
        }
    }

    private fun initGetDetailFeedStateObserver() {
        viewModel.getDetailFeedState.flowWithLifecycle(viewLifeCycle)
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
            }.launchIn(viewLifeCycleScope)
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
        Timber.d("CLICKED FEED ID: $feedId")
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
        dialog.show(parentFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun initBackButtonClickListener() {
        binding.imgMyfeedBack.setOnClickListener {
            navigateTo<MyPageFragment>()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteMyFeedState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val response = state.data ?: return@onEach
                    deletePagingDataItem(response.feedId.toInt())

                    wineySnackbar(
                        anchorView = binding.root,
                        message = stringOf(R.string.snackbar_feed_delete_success),
                        type = SnackbarType.WineyFeedResult(isSuccess = true)
                    )

                    viewModel.initDeleteFeedState()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
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

    private fun initGetMyFeedListStateObserver() {
        viewModel.getMyFeedListState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        Timber.e("PAGING DATA SUBMIT in Fragment")
                        val pagingData = state.data
                        myFeedAdapter.submitData(pagingData)
                        viewModel.initGetMyFeedListState()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
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
        viewModel.postMyFeedLikeState.flowWithLifecycle(viewLifeCycle).onEach { state ->
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
        }.launchIn(viewLifeCycleScope)
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(KEY_FEED_ID, wineyFeed.feedId)
            putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
            putExtra(KEY_PREV_SCREEN_NAME, VAL_MY_FEED_SCREEN)
            startActivity(this)
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"

        private const val POPUP_MENU_OFFSET = 65
        private const val MSG_MYFEED_ERROR = "ERROR"

        private const val TAG_FEED_DELETE_DIALOG = "DELETE_DIALOG"
        private const val VAL_MY_FEED_SCREEN = "MyFeedFragment"
    }
}
