package com.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.go.sopt.winey.domain.entity.User
import com.go.sopt.winey.domain.entity.WineyFeed
import com.go.sopt.winey.domain.repository.DataStoreRepository
import com.go.sopt.winey.presentation.main.MainViewModel
import com.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import com.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import com.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.go.sopt.winey.presentation.main.notification.NotificationActivity
import com.go.sopt.winey.util.amplitude.AmplitudeUtils
import com.go.sopt.winey.util.amplitude.type.EventType
import com.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_FEED_ITEM
import com.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_LIKE
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WineyFeedFragment :
    BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private var selectedScrollPosition: Parcelable? = null
    private var selectedItemIndex: Int = -1
    private val viewModel by viewModels<WineyFeedViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter

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
        setSwipeRefreshListener()
        initFabClickListener()
        initNotificationButtonClickListener()

        initGetFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
        removeRecyclerviewItemChangeAnimation()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getWineyFeed()
    }

    private fun restoreScrollPosition() {
        binding.rvWineyfeedPost.post {
            if (selectedItemIndex != -1) {
                binding.rvWineyfeedPost.layoutManager?.scrollToPosition(selectedItemIndex + 1)
            }
        }
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
            }
        )
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun showFeedPopupMenu(anchorView: View, wineyFeed: WineyFeed) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()
            if (isMyFeed(currentUserId, wineyFeed.userId)) {
                showFeedDeletePopupMenu(anchorView, wineyFeed)
            } else {
                showReportPopupMenu(anchorView)
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

    private fun showReportPopupMenu(anchorView: View) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        WineyPopupMenu(context = anchorView.context, titles = reportTitle) { _, _, _ ->
            showFeedReportDialog()
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun refreshWineyFeed() {
        wineyFeedHeaderAdapter.notifyItemChanged(0)
        wineyFeedAdapter.refresh()
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
            val newList = wineyFeedAdapter.deleteItem(feedId)
            wineyFeedAdapter.submitData(PagingData.from(newList))
        }
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
                        wineySnackbar(
                            binding.root,
                            true,
                            stringOf(R.string.snackbar_feed_delete_success)
                        )
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun initGetFeedStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getWineyFeedListState.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            wineyFeedAdapter.addLoadStateListener { loadState ->
                                when (loadState.refresh) {
                                    is LoadState.Loading -> {
                                        binding.rvWineyfeedPost.isVisible = false
                                    }

                                    is LoadState.NotLoading -> {
                                        binding.rvWineyfeedPost.isVisible =
                                            wineyFeedAdapter.itemCount > 0
                                        restoreScrollPosition()
                                    }

                                    is LoadState.Error -> {
                                        Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
                                    }
                                }
                            }
                            wineyFeedAdapter.submitData(state.data)
                        }

                        is UiState.Failure -> {
                            snackBar(binding.root) { state.msg }
                        }

                        else -> Timber.tag("failure").e(MSG_WINEYFEED_ERROR)
                    }
                }
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postWineyFeedLikeState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val item = wineyFeedAdapter.updateItem(
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

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bnv_main)
        bottomNav.selectedItemId = R.id.menu_mypage
    }

    private fun setSwipeRefreshListener() {
        binding.layoutWineyfeedRefresh.setOnRefreshListener {
            refreshWineyFeed()
            binding.layoutWineyfeedRefresh.isRefreshing = false
        }
    }

    private fun navigateToUpload() {
        val intent = Intent(requireContext(), UploadActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        selectedItemIndex = wineyFeedAdapter.snapshot().indexOf(wineyFeed)
        selectedScrollPosition = binding.rvWineyfeedPost.layoutManager?.onSaveInstanceState()
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(KEY_FEED_ID, wineyFeed.feedId)
        intent.putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
        startActivity(intent)
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

        private const val POPUP_MENU_OFFSET = 65

        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
    }
}
