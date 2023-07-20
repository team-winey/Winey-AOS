package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedLowlevelDeleteDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WineyFeedLowDeleteDialogFragment(private val feedId: Int) :
    BindingDialogFragment<FragmentMyfeedLowlevelDeleteDialogBinding>(R.layout.fragment_myfeed_lowlevel_delete_dialog) {
    lateinit var wineyFeedFragment: WineyFeedFragment
    private val wineyFeedViewModel by viewModels<WineyFeedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtonClickListener()
        initDeleteFeedStateObserver()
    }

    private fun initButtonClickListener() {
        binding.btnDialogCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnDialogDelete.setOnClickListener {
            wineyFeedViewModel.deleteFeed(feedId)
            initDeleteFeedStateObserver()
        }
    }

    private fun initDeleteFeedStateObserver() {
        wineyFeedFragment = WineyFeedFragment()
        wineyFeedViewModel.deleteMyFeedState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    this.dismiss()
                    refreshWineyFeed()
                    snackBar(binding.root) { state.toString() }
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }
    }

    private fun refreshWineyFeed() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            wineyFeedFragment = WineyFeedFragment()
            replace(R.id.fcv_main, wineyFeedFragment)
            commit()
        }
    }

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
    }
}