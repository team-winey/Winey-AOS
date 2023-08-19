package com.android.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
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
import com.android.go.sopt.winey.presentation.main.AlertDialogFragment
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
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.menu_wineyfeed, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        val menuDelete = popupView.findViewById<TextView>(R.id.tv_popup_delete)
        val menuReport = popupView.findViewById<TextView>(R.id.tv_popup_report)
        if (wineyFeed.userId == runBlocking { dataStoreRepository.getUserId().first() }) {
            menuReport.isVisible = false
        } else {
            menuDelete.isVisible = false
        }
        menuDelete.setOnSingleClickListener {
            showDeleteDialog(wineyFeed.feedId, wineyFeed.writerLevel)
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(view)
    }

    private fun refreshWineyFeed() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fcv_main, WineyFeedFragment())
            commit()
        }
    }

    private fun showDeleteDialog(feedId: Int, userLevel: Int) {
        val dialogSub: Int =
            if (userLevel <= LV_KNIGHT) {
                R.string.myfeed_dialog_lowlevel_sub
            } else {
                R.string.myfeed_dialog_highlevel_sub
            }

        val deleteDialog = AlertDialogFragment(
            getString(R.string.wineyfeed_dialog_title),
            getString(dialogSub),
            getString(R.string.wineyfeed_dialog_cancel),
            getString(R.string.myfeed_dialog_delete),
            handleNegativeButton = { },
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        deleteDialog.show(parentFragmentManager, TAG_DELETE_DIALOG)
        initDeleteFeedStateObserver()
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteWineyFeedState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    refreshWineyFeed()
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
                            wineyFeedAdapter.submitData(state.data)
                            wineyFeedAdapter.addLoadStateListener { loadState ->
                                wineyFeedLoadAdapter.loadState = loadState.refresh
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
            wineyFeedAdapter.refresh()
            binding.layoutWineyfeedRefresh.isRefreshing = false
        }
    }

    private fun navigateToUpload() {
        val intent = Intent(requireContext(), UploadActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val LV_KNIGHT = 2
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
        private const val MSG_WINEYFEED_ERROR = "ERROR"
        private const val TAG_DELETE_DIALOG = "DELETE_DIALOG"
    }
}
