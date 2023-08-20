import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailCommentBinding
import com.android.go.sopt.winey.domain.entity.CommentList
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class CommentAdapter : ListAdapter<CommentList, CommentAdapter.CommentViewHolder>(diffUtil) {
    class CommentViewHolder(
        private val binding: ItemDetailCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CommentList) {
            binding.apply {
                this.data = data
                executePendingBindings()
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

    companion object {
        private val diffUtil = ItemDiffCallback<CommentList>(
            onItemsTheSame = { old, new -> old.commentId == new.commentId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
