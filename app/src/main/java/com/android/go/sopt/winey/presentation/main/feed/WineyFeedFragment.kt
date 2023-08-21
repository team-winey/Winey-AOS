package com.android.go.sopt.winey.presentation.main.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
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
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainViewModel
import com.android.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import com.android.go.sopt.winey.presentation.main.feed.upload.UploadActivity
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.WineyDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.stringOf
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
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
class WineyFeedFragment :
    BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Fragment Lifecycle", "onViewCreated 호출됨")
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
            },
            toFeedDetail = { feedId, writerLevel -> navigateToDetail(feedId, writerLevel) }
        )
        binding.rvWineyfeedPost.adapter = ConcatAdapter(
            wineyFeedHeaderAdapter,
            wineyFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
        )
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(com.android.go.sopt.winey.R.layout.menu_wineyfeed, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        val menuDelete =
            popupView.findViewById<TextView>(com.android.go.sopt.winey.R.id.tv_popup_delete)
        val menuReport =
            popupView.findViewById<TextView>(com.android.go.sopt.winey.R.id.tv_popup_report)

        if (wineyFeed.userId == runBlocking { dataStoreRepository.getUserId().first() }) {
            menuReport.isVisible = false
        } else {
            menuDelete.isVisible = false
        }

        menuDelete.setOnSingleClickListener {
            showDeleteDialog(wineyFeed.feedId)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(view)
    }

    private fun refreshWineyFeed() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(com.android.go.sopt.winey.R.id.fcv_main, WineyFeedFragment())
            commit()
        }
    }

    private fun showDeleteDialog(feedId: Int) {
        val dialog = WineyDialogFragment(
            getString(R.string.feed_delete_dialog_title),
            getString(R.string.feed_delete_dialog_subtitle),
            getString(R.string.feed_delete_dialog_negative_button),
            getString(R.string.feed_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        dialog.show(parentFragmentManager, TAG_DELETE_DIALOG)

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
                    wineyFeedAdapter.refresh()
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
            val dialog = WineyDialogFragment(
                stringOf(R.string.wineyfeed_goal_dialog_title),
                stringOf(R.string.wineyfeed_goal_dialog_subtitle),
                stringOf(R.string.wineyfeed_goal_dialog_negative_button),
                stringOf(R.string.wineyfeed_goal_dialog_positive_button),
                handleNegativeButton = {},
                handlePositiveButton = { navigateTo<MyPageFragment>() }
            )
            dialog.show(parentFragmentManager, TAG_GOAL_DIALOG)
        } else {
            navigateToUpload()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(com.android.go.sopt.winey.R.id.fcv_main, T::class.simpleName)
        }
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(com.android.go.sopt.winey.R.id.bnv_main)
        bottomNav.selectedItemId = com.android.go.sopt.winey.R.id.menu_mypage
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

    private fun navigateToDetail(feedId: Int, writerLevel: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(KEY_FEED_ID, feedId)
        intent.putExtra(KEY_WRITER_LV, writerLevel)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        Log.d("Fragment Lifecycle", "onStart 호출됨")
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWineyFeed()
    }

    override fun onPause() {
        super.onPause()
        Log.d("Fragment Lifecycle", "onPause 호출됨")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Fragment Lifecycle", "onStop 호출됨")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Fragment Lifecycle", "onDestroyView 호출됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Fragment Lifecycle", "onDestroy 호출됨")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Fragment Lifecycle", "onDetach 호출됨")
    }

    companion object {
        private const val MSG_WINEYFEED_ERROR = "ERROR"
        private const val TAG_GOAL_DIALOG = "NO_GOAL_DIALOG"
        private const val TAG_DELETE_DIALOG = "DELETE_DIALOG"

        private const val KEY_FEED_ID = "feedId"
        private const val KEY_WRITER_LV = "writerLevel"
    }
}
