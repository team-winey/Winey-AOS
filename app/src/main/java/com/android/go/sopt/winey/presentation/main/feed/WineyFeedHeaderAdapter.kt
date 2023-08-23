package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemWineyfeedHeaderBinding
import java.util.Random

class WineyFeedHeaderAdapter() : RecyclerView.Adapter<WineyFeedHeaderAdapter.HeaderViewHolder>() {
    class HeaderViewHolder(
        private val binding: ItemWineyfeedHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.ivWineyfeedBanner.setImageResource(setRandomBannerImage())
        }

        private fun setRandomBannerImage(): Int {
            val random = Random()
            return when (random.nextInt(4)) {
                0 -> R.drawable.img_wineyfeed_banner_1
                1 -> R.drawable.img_wineyfeed_banner_2
                2 -> R.drawable.img_wineyfeed_banner_3
                3 -> R.drawable.img_wineyfeed_banner_4
                else -> throw IllegalArgumentException(MSG_INVALID_RANDOM_NUMBER)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemWineyfeedHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = HEADER_COUNT

    companion object {
        private const val HEADER_COUNT = 1
        private const val MSG_INVALID_RANDOM_NUMBER = "Invalid random number"
    }
}
