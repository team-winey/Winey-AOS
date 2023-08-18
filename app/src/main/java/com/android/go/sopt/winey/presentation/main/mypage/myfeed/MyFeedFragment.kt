package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.presentation.main.AlertDialogFragment
import com.android.go.sopt.winey.presentation.main.feed.WineyFeedLoadAdapter
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
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
            }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
    }

    private fun showPopupMenu(view: View, wineyFeed: WineyFeed) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.menu_wineyfeed, null,false)
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
            showDeleteDialog(wineyFeed.feedId, wineyFeed.writerLevel)
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(view)
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

    private fun initButtonClickListener() {
        binding.imgMyfeedBack.setOnSingleClickListener {
            navigateToMyPage()
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
                    myFeedAdapter.updateLikeStatus(
                        state.data.data.feedId,
                        state.data.data.isLiked
                    )
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

    private fun navigateToMyPage() {
        parentFragmentManager.commit {
            replace(R.id.fcv_main, MyPageFragment())
        }
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val LV_KNIGHT = 2
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val TAG_DELETE_DIALOG = "DELETE_DIALOG"
    }
}
