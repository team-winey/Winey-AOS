package org.go.sopt.winey.presentation.main.feed.upload.loading

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityLoadingBinding
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.main.feed.upload.AmountFragment
import org.go.sopt.winey.util.binding.BindingActivity

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    private val amountRange by lazy { resources.getIntArray(R.array.save_amount_range) }
    private val itemCategories by lazy { resources.getStringArray(R.array.save_item_categories) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifyItemCategory()
        showLottieAnimation()
    }

    private fun classifyItemCategory() {
        val amount = intent.getStringExtra(AmountFragment.KEY_SAVE_AMOUNT)?.toInt() ?: return

        for (index in amountRange.indices) {
            if (isLastCategory(index)) {
                showOtherCategoryTitle()
                binding.saveItem = itemCategories[index - 1]
                return
            }

            if (amount >= amountRange[index] && amount < amountRange[index + 1]) {
                if (index == 0) {
                    showFirstCategoryTitle()
                    return
                }

                showOtherCategoryTitle()
                binding.saveItem = itemCategories[index - 1]
                return
            }
        }
    }

    private fun isLastCategory(index: Int) = index == amountRange.size - 1

    private fun showFirstCategoryTitle() {
        binding.llLoadingTitleFirst.isVisible = true
        binding.llLoadingTitleOther.isVisible = false
    }

    private fun showOtherCategoryTitle() {
        binding.llLoadingTitleFirst.isVisible = false
        binding.llLoadingTitleOther.isVisible = true
    }

    private fun showLottieAnimation() {
        lifecycleScope.launch {
            delay(DELAY_TIME)
            navigateToMainScreen()
        }
    }

    private fun navigateToMainScreen() {
        val nowLevelUp = intent.getBooleanExtra(WineyFeedFragment.KEY_LEVEL_UP, false)

        Intent(this, MainActivity::class.java).apply {
            if (nowLevelUp) {
                putExtra(WineyFeedFragment.KEY_LEVEL_UP, true)
            } else {
                putExtra(MainActivity.KEY_FEED_UPLOAD, true)
            }
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 3000L
    }
}
