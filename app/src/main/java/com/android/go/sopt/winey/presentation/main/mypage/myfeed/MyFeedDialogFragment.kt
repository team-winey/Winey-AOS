package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentMyfeedDeleteDialogBinding
import com.android.go.sopt.winey.util.binding.BindingDialogFragment
import com.android.go.sopt.winey.util.fragment.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyFeedDialogFragment(private val feedId: Int) :
    BindingDialogFragment<FragmentMyfeedDeleteDialogBinding>(R.layout.fragment_myfeed_delete_dialog) {
    lateinit var myFeedFragment: MyFeedFragment
    private val myFeedViewModel by viewModels<MyFeedViewModel>()

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
            myFeedViewModel.deleteFeed(feedId)
            initDeleteFeedStateObserver()
        }
    }

    private fun initDeleteFeedStateObserver() {
        myFeedFragment = MyFeedFragment()
        myFeedViewModel.deleteMyFeedState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    this.dismiss()
                    /* 그리고 viewmodel 각각 만들었는데 onPause될때 viewmodel detach되므로
                    공유하려고 같이쓰는형태*/
                    /* 재생성ver */
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction().apply {
                        myFeedFragment = MyFeedFragment()
                        replace(R.id.fcv_main, myFeedFragment)
                        commit()
                    }
                    snackBar(binding.root) { state.toString() }
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