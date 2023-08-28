package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemWineyfeedHeaderBinding
import com.android.go.sopt.winey.databinding.LayoutWineyfeedBanner1Binding
import com.android.go.sopt.winey.databinding.LayoutWineyfeedBanner2Binding
import com.android.go.sopt.winey.databinding.LayoutWineyfeedBanner3Binding
import com.android.go.sopt.winey.databinding.LayoutWineyfeedBanner4Binding
import java.util.Random

class WineyFeedHeaderAdapter : RecyclerView.Adapter<WineyFeedHeaderAdapter.HeaderViewHolder>() {
    private var isInitialState = true

    inner class HeaderViewHolder(
        private val binding: ItemWineyfeedHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            if (isInitialState) {
                changeBannerLayout(0)
                isInitialState = false
            } else {
                val randomIndex = Random().nextInt(4)
                changeBannerLayout(randomIndex)
            }
        }

        private fun changeBannerLayout(index: Int) {
            binding.flWineyfeedBannerContainer.removeAllViews()
            val inflater = LayoutInflater.from(binding.root.context)

            when (index) {
                0 -> {
                    val layoutBanner1Binding =
                        LayoutWineyfeedBanner1Binding.inflate(inflater, null, false)
                    binding.flWineyfeedBannerContainer.addView(layoutBanner1Binding.root)
                }

                1 -> {
                    val layoutBanner2Binding =
                        LayoutWineyfeedBanner2Binding.inflate(inflater, null, false)
                    binding.flWineyfeedBannerContainer.addView(layoutBanner2Binding.root)
                }

                2 -> {
                    val layoutBanner3Binding =
                        LayoutWineyfeedBanner3Binding.inflate(inflater, null, false)
                    binding.flWineyfeedBannerContainer.addView(layoutBanner3Binding.root)
                }

                3 -> {
                    val layoutBanner4Binding =
                        LayoutWineyfeedBanner4Binding.inflate(inflater, null, false)
                    binding.flWineyfeedBannerContainer.addView(layoutBanner4Binding.root)
                }

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
