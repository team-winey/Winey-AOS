package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyFeedBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WineyFeedFragment : BindingFragment<FragmentWineyFeedBinding>(R.layout.fragment_winey_feed) {
    private val viewModel by viewModels<WineyFeedViewModel>()
    private lateinit var wineyFeedDialogFragment: WineyFeedDialogFragment
    private lateinit var wineyFeedAdapter: WineyFeedAdapter
    private lateinit var wineyFeedHeaderAdapter: WineyFeedHeaderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        initAdapter()
        getFeed()
        initFabClickListener()
    }

    private fun initDialog() {
        wineyFeedDialogFragment = WineyFeedDialogFragment()
        wineyFeedDialogFragment.isCancelable = false
    }

    private fun initAdapter() {
        wineyFeedAdapter = WineyFeedAdapter()
        wineyFeedHeaderAdapter = WineyFeedHeaderAdapter()
        binding.rvWineyfeedPost.adapter = ConcatAdapter(wineyFeedHeaderAdapter, wineyFeedAdapter)
    }

    private fun getFeed() {
        viewModel.getWineyFeedListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    val wineyFeedList = state.data
                    wineyFeedAdapter.submitList(wineyFeedList)
                    Timber.tag("success").e(wineyFeedList.toString())
                }

                else -> {
                }
            }
        }
    }

    private fun initFabClickListener() {
        binding.btnWineyfeedFloating.setOnSingleClickListener {
            wineyFeedDialogFragment.show(parentFragmentManager, TAG_WINEYFEED_DIALOG)
        }
    }

    companion object {
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
    }
}