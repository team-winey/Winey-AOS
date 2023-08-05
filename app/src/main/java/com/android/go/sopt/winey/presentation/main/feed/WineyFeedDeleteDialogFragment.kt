package com.android.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentFeedDeleteDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class WineyFeedDeleteDialogFragment(private val feedId: Int, private val userLevel: Int) :
    BindingDialogFragment<FragmentFeedDeleteDialogBinding>(R.layout.fragment_feed_delete_dialog) {
    lateinit var wineyFeedFragment: WineyFeedFragment
    private val wineyFeedViewModel by viewModels<WineyFeedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtonClickListener()
        initDeleteFeedStateObserver()
        setDialogSubByLevel()
    }

    private fun setDialogSubByLevel() {
        binding.tvDialogSub.text.apply {
            if (userLevel <= LV_KNIGHT) {
                getString(R.string.myfeed_dialog_lowlevel_sub)
            } else {
                getString(R.string.myfeed_dialog_highlevel_sub)
            }
        }
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
        wineyFeedViewModel.deleteMyFeedState.flowWithLifecycle(lifecycle).onEach { state ->
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
        }.launchIn(lifecycleScope)
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
        private const val LV_KNIGHT = 2
    }
}
