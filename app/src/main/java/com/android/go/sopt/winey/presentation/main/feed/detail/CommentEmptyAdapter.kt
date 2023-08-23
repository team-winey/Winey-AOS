package com.android.go.sopt.winey.presentation.main.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.LayoutCommentEmptyBinding

class CommentEmptyAdapter : RecyclerView.Adapter<CommentEmptyAdapter.CommentEmptyViewHolder>() {

    class CommentEmptyViewHolder(binding: LayoutCommentEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentEmptyViewHolder {
        return CommentEmptyViewHolder(
            LayoutCommentEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = EMPTY_ITEM_COUNT

    override fun onBindViewHolder(holder: CommentEmptyViewHolder, position: Int) {}

    companion object {
        private const val EMPTY_ITEM_COUNT = 1
    }
}
