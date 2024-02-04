package org.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
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
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.activity.showReportGoogleForm
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.amplitude.type.EventType
import org.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_FEED_ITEM
import org.go.sopt.winey.util.amplitude.type.EventType.TYPE_CLICK_LIKE
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.fragment.WineyUploadDialogFragment
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.fragment.wineySnackbar
import org.go.sopt.winey.util.view.snackbar.SnackbarType
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
        addListener()
        addObserver()
        removeRecyclerviewItemChangeAnimation()
    }

    override fun onStart() {
        super.onStart()
        if (clickedFeedId != -1) {
            Timber.d("onStart getDetailFeed")
            viewModel.getDetailFeed(clickedFeedId)
        }
    }

    private fun addListener() {
        initFabClickListener()
        initNotificationButtonClickListener()
        initSwipeRefreshListener()
        initPagingLoadStateListener()
    }

    private fun addObserver() {
        initGetWineyFeedListStateObserver()
        initGetDetailFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
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
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter(
            onBannerClicked = { ->
                initHeaderClickListener()
            }
        )
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
                saveClickedFeedId(wineyFeed.feedId)
            }
        )
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun saveClickedFeedId(feedId: Int) {
        Timber.d("CLICKED FEED ID: $feedId")
        clickedFeedId = feedId
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
        activity?.supportFragmentManager?.let { dialog.show(it, TAG_FEED_DELETE_DIALOG) }
    }

    private fun showFeedReportDialog() {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.report_dialog_title),
                stringOf(R.string.report_dialog_subtitle),
                stringOf(R.string.report_dialog_negative_button),
                stringOf(R.string.report_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = {
                requireActivity().showReportGoogleForm()
            }
        )
        activity?.supportFragmentManager?.let { dialog.show(it, TAG_FEED_REPORT_DIALOG) }
    }

    private fun isMyFeed(currentUserId: Int?, writerId: Int) = currentUserId == writerId

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteWineyFeedState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
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

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun deletePagingDataItem(feedId: Int) {
        viewLifeCycleScope.launch {
            val newList = wineyFeedAdapter.deleteItem(feedId)
            wineyFeedAdapter.submitData(PagingData.from(newList))
        }
    }

    private fun initGetWineyFeedListStateObserver() {
        viewModel.getWineyFeedListState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        Timber.e("PAGING DATA SUBMIT in Fragment")
                        val pagingData = state.data
                        wineyFeedAdapter.submitData(pagingData)
                        viewModel.initGetWineyFeedListState()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
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
                    snackBar(binding.root) { stringOf(R.string.error_winey_feed_loading) }
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
        binding.fabWineyfeedUpload.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_write_contents")
            showUploadDialog()
        }
    }

    private fun showUploadDialog() {
        val dialog = WineyUploadDialogFragment.newInstance(
            handleSaveButton = {
                initGetUserStateObserver()
            },
            handleConsumeButton = {}
        )
        activity?.supportFragmentManager?.let { dialog.show(it, TAG_UPLOAD_DIALOG) }
    }

    private fun initGetUserStateObserver() {
        viewLifeCycleScope.launch {
            mainViewModel.getUserState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        val data = dataStoreRepository.getUserInfo().firstOrNull() ?: return@collect
                        checkGoalSetting(data)
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

    private fun checkGoalSetting(data: User) {
        // 목표를 설정한 적 없거나, 기간 내 목표 달성에 실패한 경우
        if (data.isOver) {
            showDefaultGoalSettingDialog()
        }
        // 기간 내 목표 달성에 성공한 경우
        else if (data.isAttained) {
            showCongratulationDialog()
        } else { // 새 목표를 설정한 경우
            navigateToUpload()
        }
    }

    private fun showCongratulationDialog() {
        amplitudeUtils.logEvent("view_goalsetting_popup")

        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.wineyfeed_congratulation_dialog_title),
                stringOf(R.string.wineyfeed_congratulation_dialog_subtitle),
                stringOf(R.string.wineyfeed_goal_dialog_negative_button),
                stringOf(R.string.wineyfeed_goal_dialog_positive_button)
            ),
            handleNegativeButton = {
                sendDialogClickEvent(false)
            },
            handlePositiveButton = {
                sendDialogClickEvent(true)
                navigateToMyPageWithBundle()
            }
        )

        activity?.supportFragmentManager?.let { dialog.show(it, TAG_CONGRATULATION_DIALOG) }
    }

    private fun showDefaultGoalSettingDialog() {
        amplitudeUtils.logEvent("view_goalsetting_popup")

        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.wineyfeed_goal_dialog_title),
                stringOf(R.string.wineyfeed_goal_dialog_subtitle),
                stringOf(R.string.wineyfeed_goal_dialog_negative_button),
                stringOf(R.string.wineyfeed_goal_dialog_positive_button)
            ),
            handleNegativeButton = {
                sendDialogClickEvent(false)
            },
            handlePositiveButton = {
                sendDialogClickEvent(true)
                navigateToMyPageWithBundle()
            }
        )

        activity?.supportFragmentManager?.let { dialog.show(it, TAG_DEFAULT_GOAL_SETTING_DIALOG) }
    }

    private fun navigateToMyPageWithBundle() {
        val myPageFragment = MyPageFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_FROM_WINEY_FEED, true)
            }
        }
        activity?.supportFragmentManager?.commit {
            replace(R.id.fcv_main, myPageFragment)
        }
        syncBnvSelectedItem()
    }

    private fun syncBnvSelectedItem() {
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bnv_main)
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
            putExtra(KEY_PREV_SCREEN_NAME, VAL_WINEY_FEED_SCREEN)
            startActivity(this)
        }
    }

    private fun initHeaderClickListener() {
        val url = INSTAGRAM_URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
        private const val TAG_DEFAULT_GOAL_SETTING_DIALOG = "DEFAULT_GOAL_SETTING_DIALOG"
        private const val TAG_CONGRATULATION_DIALOG = "CONGRATULATION_DIALOG"
        private const val TAG_FEED_DELETE_DIALOG = "FEED_DELETE_DIALOG"
        private const val TAG_FEED_REPORT_DIALOG = "FEED_REPORT_DIALOG"
        private const val TAG_UPLOAD_DIALOG = "UPLOAD_DIALOG"
        private const val POPUP_MENU_POS_OFFSET = 65

        private const val KEY_FROM_WINEY_FEED = "fromWineyFeed"
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val VAL_WINEY_FEED_SCREEN = "WineyFeedFragment"
        private const val INSTAGRAM_URL =
            "https://instagram.com/winey__official?igshid=MzRlODBiNWFlZA=="
    }
}
