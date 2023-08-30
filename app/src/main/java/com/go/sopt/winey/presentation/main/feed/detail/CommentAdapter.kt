import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.go.sopt.winey.databinding.ItemDetailCommentBinding
import com.go.sopt.winey.domain.entity.Comment
import com.go.sopt.winey.util.view.ItemDiffCallback

class CommentAdapter(
    private val onPopupMenuClicked: (View, Int, Long) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(diffUtil) {

    inner class CommentViewHolder(
        private val binding: ItemDetailCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(comment: Comment) {
            binding.data = comment
            binding.ivCommentMore.setOnClickListener { view ->
                onPopupMenuClicked(view, comment.authorId, comment.commentId)
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

    fun addItem(item: Comment): Int {
        val newList = currentList.toMutableList()
        newList.add(item)
        submitList(newList)
        return newList.size
    }

    fun deleteItem(commentId: Long): Int {
        val comment = currentList.find { it.commentId == commentId }
        val newList = currentList.toMutableList()
        newList.remove(comment)
        submitList(newList)
        return newList.size
    }

    companion object {
        private val diffUtil = ItemDiffCallback<Comment>(
            onItemsTheSame = { old, new -> old.commentId == new.commentId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
