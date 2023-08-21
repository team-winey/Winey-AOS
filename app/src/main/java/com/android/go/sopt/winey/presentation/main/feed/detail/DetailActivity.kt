package com.android.go.sopt.winey.presentation.main.feed.detail

import CommentAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityDetailBinding
import com.android.go.sopt.winey.domain.entity.Comment
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.fragment.WineyDialogFragment
import com.android.go.sopt.winey.util.view.UiState
import com.android.go.sopt.winey.util.view.WineyPopupMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private val viewModel by viewModels<DetailViewModel>()

    private val feedId by lazy { intent.getIntExtra(KEY_FEED_ID, 0) }
    private val feedWriterId by lazy { intent.getIntExtra(KEY_FEED_WRITER_ID, 0) }

    private var detailFeedAdapter: DetailFeedAdapter? = null
    private var commentAdapter: CommentAdapter? = null
    private val commentEmptyAdapter by lazy { CommentEmptyAdapter() }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        viewModel.getFeedDetail(feedId)
        initGetFeedDetailObserver()
        initBackButtonClickListener()

        initCommentAdapter()
        initCommentCreateButtonClickListener()
        initPostCommentStateObserver()
    }

    private fun initCommentAdapter() {
        commentAdapter = CommentAdapter(
            onPopupMenuClicked = { view, commentAuthorId ->
                //showPopupMenu(view, commentAuthorId)
                showAllPopupMenu(view)
            }
        )
    }

    private fun showPopupMenu(view: View, commentAuthorId: Int) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()

            if (isMyFeed(currentUserId)) {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // todo: 내 댓글 삭제
                    showDeletePopupMenu(view)
                } else {
                    // todo: 방문자 댓글 삭제/신고
                    showAllPopupMenu(view)
                }
            } else {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // todo: 내 댓글 삭제
                    showDeletePopupMenu(view)
                } else {
                    // todo: 다른 사람 댓글 신고
                    showReportPopupMenu(view)
                }
            }
        }
    }

    private fun showDeletePopupMenu(anchorView: View) {
        val deleteTitle = listOf(stringOf(R.string.popup_delete_title))

        val popupMenu = WineyPopupMenu(
            context = anchorView.context,
            titles = deleteTitle
        ) { _, _, _ ->
            showCommentDeleteDialog()
        }

        // todo: 팝업 메뉴 표시 위치 바꾸기
        popupMenu.showAsDropDown(anchorView, 0, 0)
    }

    private fun showReportPopupMenu(view: View) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        val popupMenu =
            WineyPopupMenu(context = view.context, titles = reportTitle) { _, _, _ ->
                showCommentReportDialog()
            }

        // todo: 팝업 메뉴 표시 위치 바꾸기
        popupMenu.showAsDropDown(view)
    }

    private fun showAllPopupMenu(view: View) {
        val menuTitles = listOf(
            stringOf(R.string.popup_delete_title),
            stringOf(R.string.popup_report_title)
        )
        val popupMenu =
            WineyPopupMenu(context = view.context, titles = menuTitles) { _, _, position ->
                when (position) {
                    0 -> {
                        showCommentDeleteDialog()
                    }

                    1 -> {
                        showCommentReportDialog()
                    }
                }
            }

        // todo: 팝업 메뉴 표시 위치 바꾸기
        popupMenu.showAsDropDown(view)
    }

    private fun showCommentDeleteDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.comment_delete_dialog_title),
            stringOf(R.string.comment_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { /* todo: 댓글 삭제하기 */ }
        )
        dialog.show(supportFragmentManager, TAG_COMMENT_DELETE_DIALOG)
    }

    private fun showCommentReportDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.report_dialog_title),
            stringOf(R.string.report_dialog_subtitle),
            stringOf(R.string.report_dialog_negative_button),
            stringOf(R.string.report_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { /* todo: 댓글 신고하기 */ }
        )
        dialog.show(supportFragmentManager, TAG_COMMENT_REPORT_DIALOG)
    }

    private fun isMyFeed(currentUserId: Int?) = currentUserId == feedWriterId

    private fun isMyComment(currentUserId: Int?, commentAuthorId: Int) =
        currentUserId == commentAuthorId

    private fun initCommentCreateButtonClickListener() {
        binding.tvCommentCreate.setOnClickListener {
            val content = binding.etComment.text.toString()
            viewModel.postComment(feedId, content)
        }
    }

    private fun initPostCommentStateObserver() {
        viewModel.postCommentState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val comment = state.data ?: return@onEach
                        commentAdapter?.addItem(comment)
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun initGetFeedDetailObserver() {
        viewModel.getFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val detailFeed = state.data
                    initDetailFeedAdapter(detailFeed)
                    switchCommentContainer(detailFeed?.commentList)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initDetailFeedAdapter(detailFeed: DetailFeed?) {
        if (detailFeed == null) {
            Timber.e("DETAIL FEED IS NULL")
            return
        }
        detailFeedAdapter = DetailFeedAdapter(detailFeed)
    }

    private fun switchCommentContainer(commentList: List<Comment>?) {
        if (commentList == null) {
            Timber.e("DETAIL COMMENT LIST IS NULL")
            return
        }

        if (commentList.isEmpty()) {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentEmptyAdapter)
        } else {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentAdapter)
            commentAdapter?.submitList(commentList)
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivDetailBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"

        private const val TAG_COMMENT_DELETE_DIALOG = "COMMENT_DELETE_DIALOG"
        private const val TAG_COMMENT_REPORT_DIALOG = "COMMENT_REPORT_DIALOG"
    }
}
