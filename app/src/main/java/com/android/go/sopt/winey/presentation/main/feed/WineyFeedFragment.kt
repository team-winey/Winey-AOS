package com.android.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainViewModel
import com.android.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WineyFeedFragment : BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var wineyFeedDialogFragment: WineyFeedDialogFragment
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setSwipeRefreshListener()
        initFabClickListener()
        initPostLikeStateObserver()
        initGetFeedStateObserver()
    }

    private fun initAdapter() {
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter()
        wineyFeedLoadAdapter = WineyFeedLoadAdapter()
        wineyFeedAdapter = WineyFeedAdapter(
            likeButtonClick = { feedId, isLiked ->
                viewModel.likeFeed(feedId, isLiked)
            },
            showPopupMenu = { view, wineyFeed ->
                showPopupMenu(view, wineyFeed)
            }
        )
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_wineyfeed, popupMenu.menu)
        val menuDelete = popupMenu.menu.findItem(R.id.menu_delete)
        val menuReport = popupMenu.menu.findItem(R.id.menu_report)
        if (wineyFeed.userId == runBlocking { dataStoreRepository.getUserId().first() }) {
            menuReport.isVisible = false
        } else {
            menuDelete.isVisible = false
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    showDeleteDialog(wineyFeed.feedId, wineyFeed.writerLevel)
                    true
                }

                else -> false
                /* 신고 구현 : 앱잼 내에서는 없음 */
            }
        }
        popupMenu.show()
    }

    private fun showDeleteDialog(feedId: Int, userLevel: Int) {
        val wineyFeedDeleteDialogFragment = WineyFeedDeleteDialogFragment(feedId, userLevel)
        wineyFeedDeleteDialogFragment.show(parentFragmentManager, TAG_DELETE_DIALOG)
    }

    private fun initGetFeedStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getWineyFeedListState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                wineyFeedAdapter.submitData(state.data)
                                wineyFeedAdapter.addLoadStateListener { loadStates ->
                                    wineyFeedLoadAdapter.loadState = loadStates.refresh
                                }
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
    }

    private fun initPostLikeStateObserver() {
        viewModel.postWineyFeedLikeState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    initGetFeedStateObserver()
                    wineyFeedAdapter.updateLikeStatus(
                        state.data.data.feedId,
                        state.data.data.isLiked
                    )
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
            initGetUserStateObserver()
        }
    }

    private fun initGetUserStateObserver() {
        lifecycleScope.launch {
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

    private fun isGoalValid(data: User?) {
        if (data?.isOver == true) {
            wineyFeedDialogFragment = WineyFeedDialogFragment()
            wineyFeedDialogFragment.show(parentFragmentManager, TAG_WINEYFEED_DIALOG)
        } else {
            navigateToUpload()
        }
    }

    private fun setSwipeRefreshListener() {
        binding.layoutWineyfeedRefresh.setOnRefreshListener {
            viewModel.getWineyFeed()
            binding.layoutWineyfeedRefresh.isRefreshing = false
        }
    }

    private fun navigateToUpload() {
        val intent = Intent(requireContext(), UploadActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
        private const val MSG_WINEYFEED_ERROR = "ERROR"
        private const val TAG_DELETE_DIALOG = "DELETE_DIALOG"
    }
}
