package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.presentation.main.feed.WineyFeedLoadAdapter
import com.android.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.WineyDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedAdapter: MyFeedAdapter
    private lateinit var wineyFeedLoadAdapter: WineyFeedLoadAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initGetFeedStateObserver()
        initPostLikeStateObserver()
        initButtonClickListener()
    }

    private fun initAdapter() {
        wineyFeedLoadAdapter = WineyFeedLoadAdapter()
        myFeedAdapter = MyFeedAdapter(
            likeButtonClick = { feedId, isLiked ->
                viewModel.likeFeed(feedId, isLiked)
            },
            showPopupMenu = { view, wineyFeed ->
                showPopupMenu(view, wineyFeed)
            },
            toFeedDetail = { feedId, writerLevel -> navigateToDetail(feedId, writerLevel) }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.menu_wineyfeed, null, false)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        val menuDelete = popupView.findViewById<TextView>(R.id.tv_popup_delete)
        val menuReport = popupView.findViewById<TextView>(R.id.tv_popup_report)
        menuReport.isVisible = false

        menuDelete.setOnSingleClickListener {
            showDeleteDialog(wineyFeed.feedId)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(view)
    }

    private fun showDeleteDialog(feedId: Int) {
        val deleteDialog = WineyDialogFragment(
            getString(R.string.feed_delete_dialog_title),
            getString(R.string.feed_delete_dialog_subtitle),
            getString(R.string.feed_delete_dialog_negative_button),
            getString(R.string.feed_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        deleteDialog.show(parentFragmentManager, TAG_DELETE_DIALOG)
        initDeleteFeedStateObserver()
    }

    private fun initButtonClickListener() {
        binding.imgMyfeedBack.setOnSingleClickListener {
            navigateTo<MyPageFragment>()
            parentFragmentManager.popBackStack()
        }
    }

    private fun checkAndSetEmptyLayout() {
        if (myFeedAdapter.itemCount == 0) {
            binding.rvMyfeedPost.isVisible = false
            binding.lMyfeedEmpty.isVisible = true
        } else {
            binding.rvMyfeedPost.isVisible = true
            binding.lMyfeedEmpty.isVisible = false
        }
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteMyFeedState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    refreshMyFeed()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
    }

    private fun initGetFeedStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMyFeedListState.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            checkAndSetEmptyLayout()
                            myFeedAdapter.addLoadStateListener { loadState ->
                                wineyFeedLoadAdapter.loadState = loadState.refresh
                                checkAndSetEmptyLayout()
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
                    initGetFeedStateObserver()
                    myFeedAdapter.refresh()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }.launchIn(viewLifeCycleScope)
    }

    private fun refreshMyFeed() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fcv_main, MyFeedFragment())
            commit()
        }
    }

    private fun navigateToDetail(feedId: Int, writerLevel: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("feedId", feedId)
        intent.putExtra("writerLevel", writerLevel)
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
        private const val LV_KNIGHT = 2
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val TAG_DELETE_DIALOG = "DELETE_DIALOG"
    }
}
