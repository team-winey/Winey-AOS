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
    private lateinit var myFeedDialogFragment: MyFeedDialogFragment
    private lateinit var myFeedAdapter: MyFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initDialog()
        initGetFeedStateObserver()
    }

    private fun initAdapter() {
        myFeedAdapter = MyFeedAdapter(parentFragmentManager)
        binding.rvMyfeedPost.adapter = myFeedAdapter
    }

    private fun initDialog() {
        myFeedDialogFragment = MyFeedDialogFragment()
        myFeedDialogFragment.isCancelable = false
    }

    private fun initGetFeedStateObserver() {
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

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
    }
}
