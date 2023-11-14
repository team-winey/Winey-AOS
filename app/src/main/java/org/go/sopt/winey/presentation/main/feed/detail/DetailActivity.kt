package org.go.sopt.winey.presentation.main.feed.detail

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityDetailBinding
import org.go.sopt.winey.domain.entity.Comment
import org.go.sopt.winey.domain.entity.DetailFeed
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.activity.hideKeyboard
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.amplitude.type.EventType
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.colorOf
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.context.wineySnackbar
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.WineyPopupMenu
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BindingActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private val viewModel by viewModels<DetailViewModel>()
    private val feedId by lazy { intent.getIntExtra(KEY_FEED_ID, 0) }
    private val feedWriterId by lazy { intent.getIntExtra(KEY_FEED_WRITER_ID, 0) }
    private val prevScreenName by lazy { intent.extras?.getString(KEY_PREV_SCREEN_NAME, "") }

    private var _detailFeedAdapter: DetailFeedAdapter? = null
    private val detailFeedAdapter get() = requireNotNull(_detailFeedAdapter)
    private var _commentAdapter: CommentAdapter? = null
    private val commentAdapter get() = requireNotNull(_commentAdapter)
    private val commentEmptyAdapter by lazy { CommentEmptyAdapter() }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.getFeedDetail(feedId)

        initGetFeedDetailObserver()
        initPostLikeStateObserver()
        initDeleteFeedStateObserver()
        initPostCommentStateObserver()
        initDeleteCommentStateObserver()

        initCommentAdapter()
        initCommentCreateButtonClickListener()
        initEditTextFocusChangeListener()
        updateStatusBarColor()

        removeRecyclerviewItemChangeAnimation()
        initBackButtonClickListener()
    }

    private fun updateStatusBarColor() {
        window.statusBarColor = colorOf(R.color.white)
    }

    private fun initEditTextFocusChangeListener() {
        binding.etComment.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val commentListSize = commentAdapter.currentList.size
                binding.rvDetail.smoothScrollToPosition(commentListSize + 1)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    val focusView = Rect()
                    view.getGlobalVisibleRect(focusView)

                    val touchedX = event.x.toInt()
                    val touchedY = event.y.toInt()

                    if (!focusView.contains(touchedX, touchedY)) {
                        hideKeyboard()
                        binding.etComment.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
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
                showReportPopupMenu(anchorView)
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
                    showDeleteReportPopupMenu(anchorView, commentId)
                }
            } else {
                if (isMyComment(currentUserId, commentAuthorId)) {
                    // 내 댓글 삭제
                    showCommentDeletePopupMenu(anchorView, commentId)
                } else {
                    // 다른 사람 댓글 신고
                    showReportPopupMenu(anchorView)
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

    private fun showDeleteReportPopupMenu(anchorView: View, commentId: Long) {
        val menuTitles = listOf(
            stringOf(R.string.popup_delete_title),
            stringOf(R.string.popup_report_title)
        )
        WineyPopupMenu(context = anchorView.context, titles = menuTitles) { _, _, position ->
            when (position) {
                0 -> showCommentDeleteDialog(commentId)
                1 -> showReportDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showFeedDeleteDialog() {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.feed_delete_dialog_title),
                stringOf(R.string.feed_delete_dialog_subtitle),
                stringOf(R.string.comment_delete_dialog_negative_button),
                stringOf(R.string.comment_delete_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteFeed(feedId) }
        )
        dialog.show(supportFragmentManager, TAG_FEED_DELETE_DIALOG)
    }

    private fun showCommentDeleteDialog(commentId: Long) {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.comment_delete_dialog_title),
                stringOf(R.string.comment_delete_dialog_subtitle),
                stringOf(R.string.comment_delete_dialog_negative_button),
                stringOf(R.string.comment_delete_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = { viewModel.deleteComment(commentId) }
        )
        dialog.show(supportFragmentManager, TAG_COMMENT_DELETE_DIALOG)
    }

    private fun showReportPopupMenu(anchorView: View) {
        val reportTitle = listOf(stringOf(R.string.popup_report_title))
        WineyPopupMenu(context = anchorView.context, titles = reportTitle) { _, _, _ ->
            showReportDialog()
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showReportDialog() {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.report_dialog_title),
                stringOf(R.string.report_dialog_subtitle),
                stringOf(R.string.report_dialog_negative_button),
                stringOf(R.string.report_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = {
                // TODO: 신고하기 구글폼 연결
                /*
                val url = TERMS_URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                 */

//                when (target) {
//                    TARGET_DETAIL_FEED -> navigateToMainWithBundle(EXTRA_REPORT_KEY)
//                    TARGET_COMMENT -> showCommentReportSnackbar()
//                }
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
            val content = binding.etComment.text.toString()
            viewModel.postComment(feedId, content)
        }
    }

    private fun initGetFeedDetailObserver() {
        viewModel.getFeedDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val detailFeed = state.data ?: return@onEach
                    initDetailFeedAdapter(detailFeed)

                    val commentList = detailFeed.commentList
                    initRecyclerViewAdapter(commentList)

                    sendEventToAmplitude(EventType.TYPE_VIEW_SCREEN, detailFeed)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                    finish()
                }

                else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
            }
        }.launchIn(lifecycleScope)
    }

    private fun initRecyclerViewAdapter(commentList: List<Comment>) {
        if (commentList.isEmpty()) {
            setupEmptyCommentConcatAdapter()
        } else {
            setupCommentListConcatAdapter()
            commentAdapter.submitList(commentList)
        }
    }

    private fun switchRecyclerViewAdapter(action: String) {
        when (action) {
            ACTION_COMMENT_POST -> setupCommentListConcatAdapter()
            ACTION_COMMENT_DELETE -> setupEmptyCommentConcatAdapter()
        }
    }

    private fun setupCommentListConcatAdapter() {
        binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentAdapter)
    }

    private fun setupEmptyCommentConcatAdapter() {
        binding.rvDetail.adapter = ConcatAdapter(detailFeedAdapter, commentEmptyAdapter)
    }

    private fun initPostLikeStateObserver() {
        viewModel.postFeedDetailLikeState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val isLiked = state.data.data.isLiked
                    val likes = state.data.data.likes.toLong()
                    val feed = detailFeedAdapter.updateLikeNumber(isLiked, likes)
                    sendEventToAmplitude(EventType.TYPE_CLICK_LIKE, feed)
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
                    navigateToMainWithBundle()
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

                        if (isCommentListEmpty()) {
                            switchRecyclerViewAdapter(ACTION_COMMENT_POST)
                        }

                        val commentNumber = commentAdapter.addItem(comment)
                        detailFeedAdapter.updateCommentNumber(commentNumber.toLong())

                        binding.etComment.text.clear()
                        binding.rvDetail.smoothScrollToPosition(commentNumber + 1)
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> Timber.tag("failure").e(MSG_DETAIL_ERROR)
                }
            }.launchIn(lifecycleScope)
    }

    private fun isCommentListEmpty() = commentAdapter.currentList.isEmpty()

    private fun initDeleteCommentStateObserver() {
        viewModel.deleteCommentState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (state.data == null) return@onEach

                        val commentId = state.data.commentId
                        val commentNumber = commentAdapter.deleteItem(commentId)
                        detailFeedAdapter.updateCommentNumber(commentNumber.toLong())

                        if (isCommentNumberZero(commentNumber)) {
                            switchRecyclerViewAdapter(ACTION_COMMENT_DELETE)
                        }

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

    private fun isCommentNumberZero(commentNumber: Int) = commentNumber == 0

    private fun navigateToMainWithBundle() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(KEY_PREV_SCREEN_NAME, prevScreenName)
            putExtra(KEY_FEED_DELETE, true)
            startActivity(this)
        }
    }

    private fun WineyPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_POS_OFFSET, -POPUP_MENU_POS_OFFSET, Gravity.END)
    }

    private fun sendEventToAmplitude(type: EventType, feed: DetailFeed) {
        val eventProperties = JSONObject()

        try {
            eventProperties.put("article_id", feed.feedId)
                .put("like_count", feed.likes)
                .put("comment_count", feed.comments)

            if (type == EventType.TYPE_CLICK_LIKE) {
                eventProperties.put("from", "article")
            }
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        when (type) {
            EventType.TYPE_VIEW_SCREEN -> amplitudeUtils.logEvent(
                "view_detail_contents",
                eventProperties
            )

            EventType.TYPE_CLICK_LIKE -> amplitudeUtils.logEvent("click_like", eventProperties)
            else -> {}
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_FEED_WRITER_ID = "feedWriterId"
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val KEY_FEED_DELETE = "delete"

        private const val TAG_FEED_DELETE_DIALOG = "FEED_DELETE_DIALOG"
        private const val TAG_COMMENT_DELETE_DIALOG = "COMMENT_DELETE_DIALOG"
        private const val TAG_REPORT_DIALOG = "REPORT_DIALOG"

        private const val POPUP_MENU_POS_OFFSET = 65
        private const val MSG_DETAIL_ERROR = "ERROR"

        private const val ACTION_COMMENT_POST = "POST"
        private const val ACTION_COMMENT_DELETE = "DELETE"

        private const val REPORT_URL = "https://docs.google.com/forms/d/1fymNx8ALanWWzwR4O2s8hpt76mnRClOmfDx4Vbdk2kk"
    }
}
