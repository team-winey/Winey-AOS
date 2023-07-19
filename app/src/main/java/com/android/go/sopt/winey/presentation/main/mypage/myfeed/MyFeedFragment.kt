package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyFeedFragment : BindingFragment<FragmentMyfeedBinding>(R.layout.fragment_myfeed) {
    private val viewModel by viewModels<MyFeedViewModel>()
    private val myFeedDialogViewModel by viewModels<MyFeedDialogViewModel>()
    private lateinit var myFeedDeleteDialogFragment: MyFeedDeleteDialogFragment
    private lateinit var myFeedAdapter: MyFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initGetFeedStateObserver()
    }

    private fun initAdapter() {
        myFeedAdapter = MyFeedAdapter(parentFragmentManager) { feedId ->
            initDialog(feedId)
        }
        binding.rvMyfeedPost.adapter = myFeedAdapter
    }

    private fun initDialog(feedId: Int) {
        myFeedDeleteDialogFragment = MyFeedDeleteDialogFragment(feedId)
        myFeedDeleteDialogFragment.isCancelable = false
    }

    fun initGetFeedStateObserver() {
        viewModel.getMyFeedListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
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


    override fun onResume() {
        /* dimsiss 후 다시불러올수있도록 */
        super.onResume()
        viewModel.getMyFeed()
        initGetFeedStateObserver()
    }

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
    }

}