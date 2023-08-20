package com.android.go.sopt.winey.presentation.main.feed.detail

//import CommentAdapter
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.flowWithLifecycle
//import com.android.go.sopt.winey.R
//import com.android.go.sopt.winey.databinding.FragmentDetailBinding
//import com.android.go.sopt.winey.util.binding.BindingFragment
//import com.android.go.sopt.winey.util.fragment.snackBar
//import com.android.go.sopt.winey.util.fragment.viewLifeCycle
//import com.android.go.sopt.winey.util.fragment.viewLifeCycleScope
//import com.android.go.sopt.winey.util.view.UiState
//import com.android.go.sopt.winey.util.view.setOnSingleClickListener
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import timber.log.Timber
//
//@AndroidEntryPoint
//class DetailFragment :
//    BindingFragment<FragmentDetailBinding>(R.layout.fragment_detail) {
//    private val viewModel by viewModels<DetailViewModel>()
//
//    private var feedId: Int = 0
//    private var writerLevel: Int = 0
//    private lateinit var commentAdapter: CommentAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        arguments?.let {
//            feedId = it.getInt("feedId", 0)
//            writerLevel = it.getInt("writerLevel", 0)
//        }
//
//        binding.ivDetailBack.setOnSingleClickListener {
//            requireActivity().finish()
//        }
//
//        viewModel.getFeedDetail(feedId)
//        initAdapter()
//        initGetFeedDetailObserver()
//    }
//
//    private fun initAdapter() {
//        commentAdapter = CommentAdapter()
//        binding.rvDetailComment.adapter = commentAdapter
//        binding.ivDetailProfile.setImageResource(setUserProfile(writerLevel))
//    }
//
//    private fun initGetFeedDetailObserver() {
//        viewModel.getFeedDetailState.flowWithLifecycle(viewLifeCycle).onEach { state ->
//            when (state) {
//                is UiState.Success -> {
//                    if (state.data?.commentList?.isEmpty() == true) {
//                        binding.rvDetailComment.isVisible = false
//                        binding.lCommentEmpty.isVisible = true
//                    } else {
//                        binding.rvDetailComment.isVisible = true
//                    }
//                    binding.data = state.data
//                    commentAdapter.submitList(state.data?.commentList)
//                }
//
//                is UiState.Loading -> {
//                    binding.rvDetailComment.isVisible = false
//                    binding.lCommentEmpty.isVisible = false
//                }
//
//                is UiState.Failure -> {
//                    snackBar(binding.root) { state.msg }
//                }
//
//                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
//            }
//        }.launchIn(viewLifeCycleScope)
//    }
//
//    private fun setUserProfile(userLevel: Int): Int {
//        return when (userLevel) {
//            1 -> R.drawable.img_wineyfeed_profile_1
//            2 -> R.drawable.img_wineyfeed_profile_2
//            3 -> R.drawable.img_wineyfeed_profile_3
//            4 -> R.drawable.img_wineyfeed_profile_4
//            else -> R.drawable.img_wineyfeed_profile
//        }
//    }
//
//    companion object {
//        private const val MSG_DETAIL_ERROR = "ERROR"
//    }
//}
