package org.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentWineyFeedBinding
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import org.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import org.go.sopt.winey.presentation.main.mypage.goal.GoalPathActivity
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
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.WineyPopupMenu
import org.go.sopt.winey.util.view.setOnSingleClickListener
import org.go.sopt.winey.util.view.snackbar.SnackbarType
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
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private var wineyFeedGoalAdapter: WineyFeedGoalAdapter? = null
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
        initGetUserStateObserver()
        initGetWineyFeedListStateObserver()
        initGetDetailFeedStateObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
        initLevelUpStateObserver()
    }

    /** Adapter */
    private fun initAdapter() {
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter(
            onBannerClicked = {
                navigateToWineyInstagram()
            }
        )
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
        wineyFeedLoadAdapter = WineyFeedLoadAdapter()
        initConcatAdapter()
    }

    private fun initConcatAdapter() {
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun navigateToWineyInstagram() {
        val url = INSTAGRAM_URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun saveClickedFeedId(feedId: Int) {
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

    /** Listener */
    private fun initFabClickListener() {
        binding.fabWineyfeedUpload.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_write_contents")
            showUploadDialog()
        }
    }

    private fun showUploadDialog() {
        val dialog = WineyUploadDialogFragment.newInstance(
            handleSaveButton = { navigateToUpload(WineyFeedType.SAVE) },
            handleConsumeButton = { navigateToUpload(WineyFeedType.CONSUME) }
        )
        activity?.supportFragmentManager?.let { dialog.show(it, TAG_UPLOAD_DIALOG) }
    }

    private fun initNotificationButtonClickListener() {
        binding.ivWineyfeedNotification.setOnClickListener {
            mainViewModel.patchCheckAllNoti()
            val intent = Intent(context, NotificationActivity::class.java)
            startActivity(intent)
        }
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

    /** Observer */
    private fun initGetUserStateObserver() {
        mainViewModel.getUserState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Loading -> {
                        showLoadingProgressBar()
                    }

                    is UiState.Success -> {
                        val userInfo = state.data ?: return@onEach

                        if (wineyFeedGoalAdapter == null) {
                            updateConcatAdapter(userInfo)
                        } else {
                            // 피드 생성, 삭제에 따라 유저 데이터와 프로그레스바 갱신
                            wineyFeedGoalAdapter?.updateGoalProgressBar(userInfo)
                        }

                        dismissLoadingProgressBar()
                    }

                    is UiState.Failure -> {
                        dismissLoadingProgressBar()
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun updateConcatAdapter(user: UserV2) {
        wineyFeedGoalAdapter = WineyFeedGoalAdapter(requireContext(), user)
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedGoalAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun showLoadingProgressBar() {
        binding.pbWineyfeedLoading.isVisible = true
        binding.rvWineyfeedPost.isVisible = false
    }

    private fun dismissLoadingProgressBar() {
        binding.pbWineyfeedLoading.isVisible = false
        binding.rvWineyfeedPost.isVisible = true
    }

    private fun initGetWineyFeedListStateObserver() {
        viewModel.getWineyFeedListState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val pagingData = state.data
                        wineyFeedAdapter.submitData(pagingData)

                        // 상세피드 들어갔다 나올 때, 성공 상태가 계속 관찰되어
                        // 이미 삭제된 항목이 되살아나는 일이 없도록 UiState 초기화
                        viewModel.initGetWineyFeedState()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {}
                }
            }.launchIn(viewLifeCycleScope)
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

                        mainViewModel.getUser()

                        // 상세피드 들어갔다 나올 때 성공 상태가 계속 관찰되어
                        // 스낵바가 반복해서 뜨지 않도록 UiState 초기화
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

    private fun initLevelUpStateObserver() {
        mainViewModel.levelUpState.flowWithLifecycle(viewLifeCycle)
            .onEach { nowLevelUp ->
                if (nowLevelUp) {
                    showCongratulationDialog()
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun showCongratulationDialog() {
        val dialog = WineyDialogFragment.newInstance(
            wineyDialogLabel = WineyDialogLabel(
                title = stringOf(R.string.wineyfeed_congratulation_dialog_title),
                subTitle = stringOf(R.string.wineyfeed_congratulation_dialog_subtitle),
                positiveButtonLabel = stringOf(R.string.wineyfeed_congratulation_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = {
                navigateToGoalPath()
            }
        )
        activity?.supportFragmentManager?.let { dialog.show(it, TAG_CONGRATULATION_DIALOG) }
    }

    /** Navigation */
    private fun navigateToUpload(feedType: WineyFeedType) {
        Intent(requireContext(), UploadActivity::class.java).apply {
            putExtra(KEY_FEED_TYPE, feedType)
            startActivity(this)
        }
    }

    private fun navigateToDetail(wineyFeed: WineyFeed) {
        Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(KEY_FEED_ID, wineyFeed.feedId)
            putExtra(KEY_FEED_WRITER_ID, wineyFeed.userId)
            putExtra(KEY_PREV_SCREEN_NAME, WINEY_FEED_SCREEN)
            startActivity(this)
        }
    }

    private fun navigateToGoalPath() {
        Intent(requireContext(), GoalPathActivity::class.java).apply {
            putExtra(KEY_LEVEL_UP, true)
            startActivity(this)
        }
    }

    /** Other */
    private fun removeRecyclerviewItemChangeAnimation() {
        val animator = binding.rvWineyfeedPost.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    /** Amplitude Event Tagging */
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
        private const val INSTAGRAM_URL =
            "https://instagram.com/winey__official?igshid=MzRlODBiNWFlZA=="
        private const val MSG_WINEYFEED_ERROR = "ERROR"

        private const val TAG_FEED_DELETE_DIALOG = "FEED_DELETE_DIALOG"
        private const val TAG_FEED_REPORT_DIALOG = "FEED_REPORT_DIALOG"
        private const val TAG_UPLOAD_DIALOG = "UPLOAD_DIALOG"
        private const val TAG_CONGRATULATION_DIALOG = "CONGRATULATION_DIALOG"

        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val WINEY_FEED_SCREEN = "WineyFeedFragment"
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"

        const val KEY_FEED_TYPE = "feedType"
        const val KEY_LEVEL_UP = "LEVEL_UP_MOMENT"
    }
}
