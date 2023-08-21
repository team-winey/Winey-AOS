import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailCommentBinding
import com.android.go.sopt.winey.domain.entity.Comment
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class CommentAdapter(
    private val onPopupMenuClicked: (View, Int) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(diffUtil) {
    inner class CommentViewHolder(
        private val binding: ItemDetailCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(comment: Comment) {
            binding.apply {
                this.data = comment

                // 팝업 메뉴 버튼을 클릭하면, 해당 버튼의 뷰와 댓글 작성자의 아이디를 전달한다.
                ivCommentMore.setOnClickListener { view ->
                    onPopupMenuClicked(view, comment.authorId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val binding =
            ItemDetailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun addItem(item: Comment) {
        val newList = currentList.toMutableList()
        newList.add(item)
        submitList(newList)
    }

    // todo: 어댑터에서 아이템 삭제한 뒤에, 실제로 서버통신도 해줘야 다음에 GET 할 때 반영된다!
    fun deleteItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    companion object {
        private val diffUtil = ItemDiffCallback<Comment>(
            onItemsTheSame = { old, new -> old.commentId == new.commentId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
