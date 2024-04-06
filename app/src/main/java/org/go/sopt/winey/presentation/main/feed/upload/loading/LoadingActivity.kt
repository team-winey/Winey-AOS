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
import org.go.sopt.winey.presentation.model.WineyFeedType
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.intent.getCompatibleSerializableExtra

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    val amount = intent.getIntExtra(AmountFragment.KEY_AMOUNT, 0)
    private val feedType: WineyFeedType? by lazy {
        intent.getCompatibleSerializableExtra(
            WineyFeedFragment.KEY_FEED_TYPE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categorizeSaveItems()
        categorizeConsumeItems()

        showLottieAnimation()
    }

    private fun categorizeSaveItems() {
        val saveAmountRange = resources.getIntArray(R.array.save_amount_range)
        val saveItems = resources.getStringArray(R.array.save_item_categories)

        if (amount >= saveAmountRange.last()) {
            showLastItem(name = saveItems.last())
            return
        }

        for (index in 0 until saveAmountRange.lastIndex) {
            if (amount in saveAmountRange[index] until saveAmountRange[index + 1]) {
                if (index == 0) {
                    showFirstCategoryTitle()
                    return
                }

                showOtherCategoryTitle()
                binding.itemName = saveItems[index - 1]
                return
            }
        }
    }

    private fun showLastItem(name: String) {
        showOtherCategoryTitle()
        binding.itemName = name
    }

    private fun categorizeConsumeItems() {

    }

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
