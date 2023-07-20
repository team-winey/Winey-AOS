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
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private lateinit var myFeedDeleteLowDialogFragment: MyFeedDeleteLowDialogFragment
    private lateinit var myFeedDeleteHighDialogFragment: MyFeedDeleteHighDialogFragment
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
        myFeedAdapter = MyFeedAdapter(deleteButtonClick = { feedId, writerLevel ->
            initDialog(feedId, writerLevel)
        }, fragmentManager = parentFragmentManager, likeButtonClick = { feedId, isLiked ->
            viewModel.likeFeed(feedId, isLiked)
        })
        binding.rvMyfeedPost.adapter = myFeedAdapter
    }

    private fun initDialog(feedId: Int, writerLevel: Int) {
        myFeedDeleteLowDialogFragment = MyFeedDeleteLowDialogFragment(feedId)
        myFeedDeleteLowDialogFragment.isCancelable = false

        myFeedDeleteHighDialogFragment = MyFeedDeleteHighDialogFragment(feedId)
        myFeedDeleteHighDialogFragment.isCancelable = false
    }

    private fun initEmptyItemLayout(totalPage: Int) {
        if (totalPage == 0) {
            binding.rvMyfeedPost.isVisible = false
        } else {
            binding.layoutMyfeedEmpty.isVisible = false
        }
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
                        totalPage = 0
                    } else initEmptyItemLayout(state.data[0].totalPageSize)
                    val myFeedList = state.data
                    myFeedAdapter.submitList(myFeedList)
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
                    viewModel.getMyFeed()
                    initGetFeedStateObserver()
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
                        viewModel.getMyFeed()
                        initGetFeedStateObserver()
                    }
                }
            }
        })
    }

    private fun navigateToMyPage() {
        parentFragmentManager.commit {
            replace(R.id.fcv_main, MyPageFragment())
            addToBackStack(null)
        }
    }


    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val MAX_FEED_VER_PAGE = 10
    }

}