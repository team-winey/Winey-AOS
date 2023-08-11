package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedDeleteDialogFragment: MyFeedDeleteDialogFragment
    private lateinit var myFeedAdapter: MyFeedAdapter
    private var totalPage = Int.MAX_VALUE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initGetFeedStateObserver()
        initPostLikeStateObserver()
        initButtonClickListener()
        setListWithInfiniteScroll()
    }

    private fun initAdapter() {
        myFeedAdapter = MyFeedAdapter(
            deleteButtonClick = { feedId, writerLevel ->
                initDialog(feedId, writerLevel)
            },
            fragmentManager = parentFragmentManager,
            likeButtonClick = { feedId, isLiked ->
                viewModel.likeFeed(feedId, isLiked)
            }
        )
        binding.rvMyfeedPost.adapter = myFeedAdapter
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

    fun initGetFeedStateObserver() {
        viewModel.getMyFeedListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        binding.rvMyfeedPost.isVisible = false
                        binding.layoutMyfeedEmpty.isVisible = true
                    } else {
                        binding.rvMyfeedPost.isVisible = true
                    }
                    val myFeedList = state.data
                    myFeedAdapter.submitList(myFeedList)
                }

                is UiState.Loading -> {
                    binding.rvMyfeedPost.isVisible = false
                    binding.layoutMyfeedEmpty.isVisible = false
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postMyFeedLikeState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    initGetFeedStateObserver()
                    myFeedAdapter.updateLikeStatus(state.data.data.feedId, state.data.data.isLiked)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }
    }

    private fun setListWithInfiniteScroll() {
        binding.rvMyfeedPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    var itemCount = myFeedAdapter.itemCount
                    var lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (binding.rvMyfeedPost.canScrollVertically(1) && lastVisibleItemPosition == itemCount) {
                        lastVisibleItemPosition += MAX_FEED_VER_PAGE
                        itemCount += MAX_FEED_VER_PAGE
                        runBlocking {
//                            viewModel.getMyFeed()
//                            delay(100)
                        }
                    }
                }
            }
        })
    }

    private fun navigateToMyPage() {
        parentFragmentManager.commit {
            replace(R.id.fcv_main, MyPageFragment())
        }
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val MAX_FEED_VER_PAGE = 10
    }
}
