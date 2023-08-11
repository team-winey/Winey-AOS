package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentFeedDeleteDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.fragment.viewLifeCycle
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class MyFeedDeleteDialogFragment(private val feedId: Int, private val userLevel: Int) :
    BindingDialogFragment<FragmentFeedDeleteDialogBinding>(R.layout.fragment_feed_delete_dialog) {
    lateinit var myFeedFragment: MyFeedFragment
    private val myFeedViewModel by viewModels<MyFeedViewModel>()

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
            myFeedViewModel.deleteFeed(feedId)
            initDeleteFeedStateObserver()
        }
    }

    private fun initDeleteFeedStateObserver() {
        myFeedViewModel.deleteMyFeedState.flowWithLifecycle(viewLifeCycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    this.dismiss()
                    refreshMyFeed()
                    snackBar(binding.root) { state.toString() }
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_MYFEED_ERROR)
            }
        }
    }

    private fun refreshMyFeed() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            myFeedFragment = MyFeedFragment()
            replace(R.id.fcv_main, myFeedFragment)
            commit()
        }
    }

    companion object {
        private const val MSG_MYFEED_ERROR = "ERROR"
        private const val LV_KNIGHT = 2
    }
}
