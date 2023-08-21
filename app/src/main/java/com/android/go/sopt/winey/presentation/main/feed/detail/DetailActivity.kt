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

        viewModel.getFeedDetail(feedId)
        initGetFeedDetailObserver()
        initBackButtonClickListener()
        initPostLikeStateObserver()

        initCommentAdapter()
        initCommentCreateButtonClickListener()
        initPostCommentStateObserver()
    }

    private fun initCommentAdapter() {
        _commentAdapter = CommentAdapter(
            onPopupMenuClicked = { anchorView, commentAuthorId ->
                showCommentPopupMenu(anchorView, commentAuthorId)
            }
        )
    }

    private fun initDetailFeedAdapter(detailFeed: DetailFeed?) {
        if (detailFeed == null) {
            Timber.e("DETAIL FEED IS NULL")
            return
        }
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

    private fun showCommentPopupMenu(anchorView: View, commentAuthorId: Int) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()

            if (isMyFeed(currentUserId)) {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // 내 댓글 삭제
                    showCommentDeletePopupMenu(anchorView)
                } else {
                    // 방문자 댓글 삭제/신고
                    showAllPopupMenu(anchorView)
                }
            } else {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // 내 댓글 삭제
                    showCommentDeletePopupMenu(anchorView)
                } else {
                    // 다른 사람 댓글 신고
                    showReportPopupMenu(anchorView)
                }
            }
        }
    }

    private fun showFeedPopupMenu(anchorView: View) {
        lifecycleScope.launch {
            val currentUserId = dataStoreRepository.getUserId().first()
            if (isMyFeed(currentUserId)) {
                showFeedDeletePopupMenu(anchorView)
            } else {
                showReportPopupMenu(anchorView)
            }
        }
    }

    private fun showCommentDeletePopupMenu(anchorView: View) {
        val deleteTitle = listOf(stringOf(R.string.popup_delete_title))
        WineyPopupMenu(context = anchorView.context, titles = deleteTitle) { _, _, _ ->
            showCommentDeleteDialog()
        }.apply {
            showCustomPosition(anchorView)
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

    private fun showReportPopupMenu(anchorView: View) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        WineyPopupMenu(context = anchorView.context, titles = reportTitle) { _, _, _ ->
            showCommentReportDialog()
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showAllPopupMenu(anchorView: View) {
        val menuTitles = listOf(
            stringOf(R.string.popup_delete_title),
            stringOf(R.string.popup_report_title)
        )
        WineyPopupMenu(context = anchorView.context, titles = menuTitles) { _, _, position ->
            when (position) {
                0 -> showCommentDeleteDialog()
                1 -> showCommentReportDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun WineyPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_OFFSET, -POPUP_MENU_OFFSET, Gravity.END)
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

    private fun showFeedDeleteDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.feed_delete_dialog_title),
            stringOf(R.string.feed_delete_dialog_subtitle),
            stringOf(R.string.comment_delete_dialog_negative_button),
            stringOf(R.string.comment_delete_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        dialog.show(supportFragmentManager, TAG_COMMENT_DELETE_DIALOG)
        initDeleteFeedStateObserver()
    }

    private fun showCommentReportDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.report_dialog_title),
            stringOf(R.string.report_dialog_subtitle),
            stringOf(R.string.report_dialog_negative_button),
            stringOf(R.string.report_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { navigateToMain(EXTRA_REPORT_KEY) }
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

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun initDeleteFeedStateObserver() {
        viewModel.deleteFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    navigateToMain(EXTRA_DELETE_KEY)
                }

                is UiState.Failure -> {
                    wineySnackbar(binding.root, false, stringOf(R.string.snackbar_delete_fail))
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
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
            commentAdapter.submitList(commentList)
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun initPostLikeStateObserver() {
        viewModel.postFeedDetailLikeState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    viewModel.getFeedDetail(feedId)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {

                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun navigateToMain(extraKey: String) {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(extraKey, true)
            startActivity(this)
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"

        private const val TAG_COMMENT_DELETE_DIALOG = "COMMENT_DELETE_DIALOG"
        private const val TAG_COMMENT_REPORT_DIALOG = "COMMENT_REPORT_DIALOG"

        private const val POPUP_MENU_OFFSET = 65

        private const val MSG_DETAIL_ERROR = "ERROR"

        private const val EXTRA_DELETE_KEY = "delete"
        private const val EXTRA_REPORT_KEY = "report"
    }
}
