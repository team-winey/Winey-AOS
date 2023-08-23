package com.android.go.sopt.winey.presentation.main.feed.detail

import CommentAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityDetailBinding
import com.android.go.sopt.winey.domain.entity.Comment
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.context.wineySnackbar
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

    private var _detailFeedAdapter: DetailFeedAdapter? = null
    private val detailFeedAdapter get() = requireNotNull(_detailFeedAdapter)

    private var _commentAdapter: CommentAdapter? = null
    private val commentAdapter get() = requireNotNull(_commentAdapter)

    private val commentEmptyAdapter by lazy { CommentEmptyAdapter() }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        removeRecyclerviewItemChangeAnimation()
        initBackButtonClickListener()

        viewModel.getFeedDetail(feedId)
        initGetFeedDetailObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()

        initCommentAdapter()
        initCommentCreateButtonClickListener()
        initPostCommentStateObserver()
        initDeleteCommentStateObserver()
    }

    private fun initBackButtonClickListener() {
        binding.ivDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun removeRecyclerviewItemChangeAnimation() {
        val animator = binding.rvDetail.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun initDetailFeedAdapter(detailFeed: DetailFeed) {
        _detailFeedAdapter =
            DetailFeedAdapter(
                detailFeed,
                onLikeButtonClicked = { feedId, isLiked ->
                    viewModel.likeFeed(feedId, isLiked)
                },
                onPopupMenuClicked = { anchorView ->
                    showFeedPopupMenu(anchorView)
                }
            )
    }

    private fun initCommentAdapter() {
        _commentAdapter = CommentAdapter(
            onPopupMenuClicked = { anchorView, commentAuthorId, commentId ->
                showCommentPopupMenu(anchorView, commentAuthorId, commentId)
            }
        )
    }

    private fun showFeedPopupMenu(anchorView: View) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()
            if (isMyFeed(currentUserId)) {
                // 내 게시물 삭제
                showFeedDeletePopupMenu(anchorView)
            } else {
                // 다른 사람 게시물 신고
                showReportPopupMenu(anchorView, TARGET_DETAIL_FEED)
            }
        }
    }

    private fun showCommentPopupMenu(anchorView: View, commentAuthorId: Int, commentId: Long) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()

            if (isMyFeed(currentUserId)) {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // 내 댓글 삭제
                    showCommentDeletePopupMenu(anchorView, commentId)
                } else {
                    // 방문자 댓글 삭제/신고
                    showAllPopupMenu(anchorView, commentId)
                }
            } else {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // 내 댓글 삭제
                    showCommentDeletePopupMenu(anchorView, commentId)
                } else {
                    // 다른 사람 댓글 신고
                    showReportPopupMenu(anchorView, TARGET_COMMENT)
                }
            }
        }
    }

    private fun showFeedDeletePopupMenu(anchorView: View) {
        val deleteTitle = listOf(stringOf(R.string.popup_delete_title))
        WineyPopupMenu(context = anchorView.context, titles = deleteTitle) { _, _, _ ->
            showFeedDeleteDialog()
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showCommentDeletePopupMenu(anchorView: View, commentId: Long) {
        val deleteTitle = listOf(stringOf(R.string.popup_delete_title))
        WineyPopupMenu(context = anchorView.context, titles = deleteTitle) { _, _, _ ->
            showCommentDeleteDialog(commentId)
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showAllPopupMenu(anchorView: View, commentId: Long) {
        val menuTitles = listOf(
            stringOf(R.string.popup_delete_title),
            stringOf(R.string.popup_report_title)
        )
        WineyPopupMenu(context = anchorView.context, titles = menuTitles) { _, _, position ->
            when (position) {
                0 -> showCommentDeleteDialog(commentId)
                1 -> showReportDialog(TARGET_COMMENT)
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showFeedDeleteDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.feed_delete_dialog_title),
            stringOf(R.string.feed_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        dialog.show(supportFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun showCommentDeleteDialog(commentId: Long) {
        val dialog = WineyDialogFragment(
            stringOf(R.string.comment_delete_dialog_title),
            stringOf(R.string.comment_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteComment(commentId) }
        )
        dialog.show(supportFragmentManager, TAG_COMMENT_DELETE_DIALOG)
    }

    private fun showReportPopupMenu(anchorView: View, target: String) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        WineyPopupMenu(context = anchorView.context, titles = reportTitle) { _, _, _ ->
            showReportDialog(target)
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    // 신고의 대상이 무엇인지에 따라 스낵바가 뜨는 위치가 달라지도록
    private fun showReportDialog(target: String) {
        val dialog = WineyDialogFragment(
            stringOf(R.string.report_dialog_title),
            stringOf(R.string.report_dialog_subtitle),
            stringOf(R.string.report_dialog_negative_button),
            stringOf(R.string.report_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = {
                when (target) {
                    TARGET_DETAIL_FEED -> navigateToMainWithBundle(EXTRA_REPORT_KEY)
                    TARGET_COMMENT -> showCommentReportSnackbar()
                }
            }
        )
        dialog.show(supportFragmentManager, TAG_REPORT_DIALOG)
    }

    private fun showCommentReportSnackbar() {
        wineySnackbar(binding.root, true, stringOf(R.string.snackbar_report_success))
    }

    private fun isMyFeed(currentUserId: Int?) = currentUserId == feedWriterId

    private fun isMyComment(currentUserId: Int?, commentAuthorId: Int) =
        currentUserId == commentAuthorId

    private fun initCommentCreateButtonClickListener() {
        binding.tvCommentCreate.setOnClickListener {
            checkEmptyCommentList()
            val content = binding.etComment.text.toString()
            viewModel.postComment(feedId, content)
        }
    }

    private fun checkEmptyCommentList() {
        if (commentAdapter.currentList.size == 0) {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentAdapter)
        }
    }

    private fun initGetFeedDetailObserver() {
        viewModel.getFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val detailFeed = state.data ?: return@onEach
                    initDetailFeedAdapter(detailFeed)

                    val commentList = detailFeed.commentList
                    switchCommentContainer(commentList)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun switchCommentContainer(commentList: List<Comment>) {
        if (commentList.isEmpty()) {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentEmptyAdapter)
        } else {
            binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentAdapter)
            commentAdapter.submitList(commentList)
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postFeedDetailLikeState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val isLiked = state.data.data.isLiked
                    val likes = state.data.data.likes.toLong()
                    detailFeedAdapter.updateLikeNumber(isLiked, likes)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    navigateToMainWithBundle(EXTRA_DELETE_KEY)
                }

                is UiState.Failure -> {
                    wineySnackbar(binding.root, false, stringOf(R.string.snackbar_delete_fail))
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun initPostCommentStateObserver() {
        viewModel.postCommentState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        val comment = state.data ?: return@onEach
                        val commentNumber = commentAdapter.addItem(comment)
                        detailFeedAdapter.updateCommentNumber(commentNumber.toLong())
                        binding.etComment.text.clear()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
                }
            }.launchIn(lifecycleScope)
    }

    private fun initDeleteCommentStateObserver() {
        viewModel.deleteCommentState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (state.data == null) return@onEach

                        val commentNumber = commentAdapter.deleteItem(state.data.commentId)
                        detailFeedAdapter.updateCommentNumber(commentNumber.toLong())

                        wineySnackbar(
                            binding.root,
                            true,
                            stringOf(R.string.snackbar_comment_delete_success)
                        )
                    }

                    is UiState.Failure -> {
                        wineySnackbar(binding.root, false, stringOf(R.string.snackbar_delete_fail))
                    }

                    else -> {
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun navigateToMainWithBundle(extraKey: String) {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(extraKey, true)
            startActivity(this)
        }
    }

    private fun WineyPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_POS_OFFSET, -POPUP_MENU_POS_OFFSET, Gravity.END)
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"

        private const val TAG_FEED_DELETE_DIALOG = "FEED_DELETE_DIALOG"
        private const val TAG_COMMENT_DELETE_DIALOG = "COMMENT_DELETE_DIALOG"
        private const val TAG_REPORT_DIALOG = "REPORT_DIALOG"

        private const val POPUP_MENU_POS_OFFSET = 65
        private const val MSG_DETAIL_ERROR = "ERROR"
        private const val EXTRA_DELETE_KEY = "delete"
        private const val EXTRA_REPORT_KEY = "report"

        private const val TARGET_DETAIL_FEED = "detailFeed"
        private const val TARGET_COMMENT = "comment"
    }
}
