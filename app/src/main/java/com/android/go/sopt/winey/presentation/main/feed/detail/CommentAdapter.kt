import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailCommentBinding
import com.android.go.sopt.winey.domain.entity.Comment
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class CommentAdapter(
    private val onPopupMenuClicked: (View, Int, Long) -> Unit,
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(diffUtil) {

    inner class CommentViewHolder(
        private val binding: ItemDetailCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position)
                }
            }
        }

        fun onBind(comment: Comment) {
            binding.apply {
                this.data = comment
                ivCommentMore.setOnClickListener { view ->
                    onPopupMenuClicked(view, comment.authorId, comment.commentId)
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

    fun addItem(item: Comment): Int {
        val newList = currentList.toMutableList()
        newList.add(item)
        submitList(newList)
        return newList.size
    }

    fun deleteItem(position: Int): Int {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
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
