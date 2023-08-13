package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.presentation.main.feed.WineyFeedLoadAdapter
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedDeleteDialogFragment: MyFeedDeleteDialogFragment
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
            deleteButtonClick = { feedId, writerLevel ->
                initDialog(feedId, writerLevel)
            },
            fragmentManager = parentFragmentManager,
            likeButtonClick = { feedId, isLiked ->
                viewModel.likeFeed(feedId, isLiked)
            }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter.withLoadStateFooter(wineyFeedLoadAdapter)
    }

    private fun initDialog(feedId: Int, userLevel: Int) {
        myFeedDeleteDialogFragment = MyFeedDeleteDialogFragment(feedId, userLevel)
        myFeedDeleteDialogFragment.isCancelable = false
    }

    private fun initButtonClickListener() {
        binding.imgMyfeedBack.setOnSingleClickListener {
            navigateToMyPage()
        }
    }

    private fun initGetFeedStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getMyFeedListState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                myFeedAdapter.submitData(state.data)
                                myFeedAdapter.addLoadStateListener { loadState ->
                                    wineyFeedLoadAdapter.loadState = loadState.refresh
                                    if (loadState.append.endOfPaginationReached) {
                                        binding.rvMyfeedPost.isVisible = false
                                        binding.layoutMyfeedEmpty.isVisible = true
                                    } else {
                                        binding.rvMyfeedPost.isVisible = true
                                    }
                                }
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
        }
    }

    private fun navigateToMyPage() {
        parentFragmentManager.commit {
            replace(R.id.fcv_main, MyPageFragment())
        }
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
    }
}
