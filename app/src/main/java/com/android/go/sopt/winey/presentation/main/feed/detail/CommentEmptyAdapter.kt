package com.android.go.sopt.winey.presentation.main.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailCommentEmptyBinding

class CommentEmptyAdapter : RecyclerView.Adapter<CommentEmptyAdapter.CommentEmptyViewHolder>() {

    class CommentEmptyViewHolder(binding: ItemDetailCommentEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentEmptyViewHolder {
        return CommentEmptyViewHolder(
            ItemDetailCommentEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = COMMENT_ITEM_COUNT

    override fun onBindViewHolder(holder: CommentEmptyViewHolder, position: Int) {}

    companion object {
        private const val COMMENT_ITEM_COUNT = 1
    }
}
