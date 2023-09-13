package org.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentWineyFeedBinding
import org.go.sopt.winey.domain.entity.User
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import org.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import org.go.sopt.winey.presentation.main.mypage.MyPageFragment
import org.go.sopt.winey.presentation.main.notification.NotificationActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.amplitude.type.EventType
import org.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_FEED_ITEM
import org.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_LIKE
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.fragment.wineySnackbar
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.WineyPopupMenu
import org.go.sopt.winey.util.view.setOnSingleClickListener
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WineyFeedFragment :
    BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter
    private var clickedFeedId = -1
    private var deleteFeedId = -1

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_homefeed")
        binding.vm = mainViewModel
        mainViewModel.getHasNewNoti()

        initAdapter()
        initFabClickListener()
        initNotificationButtonClickListener()
        removeRecyclerviewItemChangeAnimation()

        collectWineyFeedPagingData()
        initGetDetailFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()

        initSwipeRefreshListener()
        initPagingLoadStateListener()
    }

    override fun onStart() {
        super.onStart()
        if (clickedFeedId != -1) {
            viewModel.getDetailFeed(clickedFeedId)
        }
    }

    private fun initGetDetailFeedStateObserver() {
        viewModel.getDetailFeedState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val detailFeed = state.data ?: return@onEach
                        wineyFeedAdapter.updateItem(clickedFeedId, detailFeed)
                        clickedFeedId = -1
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun removeRecyclerviewItemChangeAnimation() {
        val animator = binding.rvWineyfeedPost.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun initAdapter() {
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter()
        wineyFeedLoadAdapter = WineyFeedLoadAdapter()
        wineyFeedAdapter = WineyFeedAdapter(
            onlikeButtonClicked = { wineyFeed ->
                viewModel.likeFeed(wineyFeed.feedId, !wineyFeed.isLiked)
            },
            onPopupMenuClicked = { anchorView, wineyFeed ->
                showFeedPopupMenu(anchorView, wineyFeed)
            },
            toFeedDetail = { wineyFeed ->
                sendWineyFeedEvent(TYPE_CLICK_FEED_ITEM, wineyFeed)
                navigateToDetail(wineyFeed)
                saveClickedItemId(wineyFeed)
            }
        )
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun saveClickedItemId(wineyFeed: WineyFeed) {
        clickedFeedId = wineyFeed.feedId
    }

    private fun showFeedPopupMenu(anchorView: View, wineyFeed: WineyFeed) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()
            if (isMyFeed(currentUserId, wineyFeed.userId)) {
                showFeedDeletePopupMenu(anchorView, wineyFeed)
            } else {
                showFeedReportPopupMenu(anchorView)
            }
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

    private fun showFeedReportPopupMenu(anchorView: View) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        WineyPopupMenu(context = anchorView.context, titles = reportTitle) { _, _, _ ->
            showFeedReportDialog()
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun WineyPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_POS_OFFSET, -POPUP_MENU_POS_OFFSET, Gravity.END)
    }

    private fun showFeedDeleteDialog(feed: WineyFeed) {
        val dialog = WineyDialogFragment(
            stringOf(R.string.feed_delete_dialog_title),
            stringOf(R.string.feed_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = {
                deleteFeedId = feed.feedId
                viewModel.deleteFeed(feed.feedId)
            }
        )
        dialog.show(parentFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun showFeedReportDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.report_dialog_title),
            stringOf(R.string.report_dialog_subtitle),
            stringOf(R.string.report_dialog_negative_button),
            stringOf(R.string.report_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = {
                wineySnackbar(
                    binding.root,
                    true,
                    stringOf(R.string.snackbar_report_success)
                )
            }
        )
        dialog.show(parentFragmentManager, TAG_FEED_REPORT_DIALOG)
    }

    private fun isMyFeed(currentUserId: Int?, writerId: Int) = currentUserId == writerId

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteWineyFeedState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        deletePagingDataItem()
                        wineySnackbar(
                            binding.root,
                            true,
                            stringOf(R.string.snackbar_feed_delete_success)
                        )
                        viewModel.initDeleteFeedState()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun deletePagingDataItem() {
        viewLifeCycleScope.launch {
            val newList = wineyFeedAdapter.deleteItem(deleteFeedId)
            wineyFeedAdapter.submitData(PagingData.from(newList))
            deleteFeedId = -1
        }
    }

    private fun collectWineyFeedPagingData() {
        viewLifeCycleScope.launch {
            viewModel.wineyFeedPagingData.collectLatest { pagingData ->
                Timber.e("PAGING DATA COLLECT in Fragment")
                wineyFeedAdapter.submitData(pagingData)
            }
        }
    }

    private fun initPagingLoadStateListener() {
        wineyFeedAdapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    Timber.d("LOADING")
                    binding.rvWineyfeedPost.isVisible = false
                }

                is LoadState.NotLoading -> {
                    Timber.d("NOT LOADING")
                    binding.rvWineyfeedPost.isVisible = wineyFeedAdapter.itemCount > 0
                }

                is LoadState.Error -> {
                    Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
                }
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postWineyFeedLikeState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val item = wineyFeedAdapter.updateLikeNumber(
                        state.data.data.feedId,
                        state.data.data.isLiked,
                        state.data.data.likes
                    ) ?: return@onEach

                    sendWineyFeedEvent(TYPE_CLICK_LIKE, item)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
    }

    private fun initFabClickListener() {
        binding.btnWineyfeedFloating.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_write_contents")
            initGetUserStateObserver()
        }
    }

    private fun initGetUserStateObserver() {
        viewLifeCycleScope.launch {
            mainViewModel.getUserState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        val data = dataStoreRepository.getUserInfo().firstOrNull()
                        isGoalValid(data)
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
                }
            }
        }
    }

    private fun initNotificationButtonClickListener() {
        binding.ivWineyfeedNotification.setOnClickListener {
            mainViewModel.patchCheckAllNoti()
            val intent = Intent(context, NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isGoalValid(data: User?) {
        if (data?.isOver == true) {
            showGoalSettingDialog()
        } else {
            navigateToUpload()
        }
    }

    private fun showGoalSettingDialog() {
        amplitudeUtils.logEvent("view_goalsetting_popup")

        val dialog = WineyDialogFragment(
            stringOf(R.string.wineyfeed_goal_dialog_title),
            stringOf(R.string.wineyfeed_goal_dialog_subtitle),
            stringOf(R.string.wineyfeed_goal_dialog_negative_button),
            stringOf(R.string.wineyfeed_goal_dialog_positive_button),
            handleNegativeButton = {
                sendDialogClickEvent(false)
            },
            handlePositiveButton = {
                sendDialogClickEvent(true)
                navigateTo<MyPageFragment>()
            }
        )
        dialog.show(parentFragmentManager, TAG_GOAL_DIALOG)
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bnv_main)
        bottomNav.selectedItemId = R.id.menu_mypage
    }

    private fun initSwipeRefreshListener() {
        binding.layoutWineyfeedRefresh.setOnRefreshListener {
            refreshWineyFeed()
            binding.layoutWineyfeedRefresh.isRefreshing = false
        }
    }

    private fun refreshWineyFeed() {
        wineyFeedHeaderAdapter.notifyItemChanged(0)
        wineyFeedAdapter.refresh()
    }

    private fun navigateToUpload() {
        val intent = Intent(requireContext(), UploadActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(KEY_FEED_ID, wineyFeed.feedId)
            putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
            putExtra(KEY_PREV_SCREEN, WINEY_FEED_SCREEN)
            startActivity(this)
        }
    }

    private fun sendDialogClickEvent(isNavigate: Boolean) {
        val eventProperties = JSONObject()

        try {
            if (isNavigate) {
                eventProperties.put("method", "yes")
            } else {
                eventProperties.put("method", "no")
            }
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        amplitudeUtils.logEvent("click_goalsetting", eventProperties)
    }

    private fun sendWineyFeedEvent(type: EventType, feed: WineyFeed) {
        val eventProperties = JSONObject()

        try {
            eventProperties.put("article_id", feed.feedId)
                .put("like_count", feed.likes)
                .put("comment_count", feed.comments)

            if (type == TYPE_CLICK_LIKE) {
                eventProperties.put("from", "feed")
            }
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        when (type) {
            TYPE_CLICK_FEED_ITEM -> amplitudeUtils.logEvent(
                "click_homefeed_contents",
                eventProperties
            )

            TYPE_CLICK_LIKE ->
                amplitudeUtils.logEvent("click_like", eventProperties)

            else -> {}
        }
    }

    companion object {
        private const val MSG_WINEYFEED_ERROR = "ERROR"
        private const val TAG_GOAL_DIALOG = "NO_GOAL_DIALOG"
        private const val TAG_FEED_DELETE_DIALOG = "FEED_DELETE_DIALOG"
        private const val TAG_FEED_REPORT_DIALOG = "FEED_REPORT_DIALOG"
        private const val POPUP_MENU_POS_OFFSET = 65
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val KEY_PREV_SCREEN = "PREV_SCREEN_NAME"
        private const val WINEY_FEED_SCREEN = "WineyFeedFragment"
    }
}
