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
    private val amount by lazy { intent.getIntExtra(AmountFragment.KEY_AMOUNT, 0) }
    private val feedType: WineyFeedType by lazy {
        intent.getCompatibleSerializableExtra(
            WineyFeedFragment.KEY_FEED_TYPE
        ) ?: WineyFeedType.SAVE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        switchTitle()
        switchItemCategory()
        switchLottieAnimation()
    }

    private fun switchTitle() {
        binding.tvUploadLoadingTitleLine3.text = when (feedType) {
            WineyFeedType.SAVE -> getString(R.string.upload_loading_title_other_save_line3)
            WineyFeedType.CONSUME -> getString(R.string.upload_loading_title_other_consume_line3)
        }
    }

    private fun switchItemCategory() {
        when (feedType) {
            WineyFeedType.SAVE -> {
                val saveAmountRange = resources.getIntArray(R.array.save_amount_range)
                val saveItems = resources.getStringArray(R.array.save_item_categories)
                categorizeItemsByAmount(amountRange = saveAmountRange, items = saveItems)
            }

            WineyFeedType.CONSUME -> {
                val consumeAmountRange = resources.getIntArray(R.array.consume_amount_range)
                val consumeItems = resources.getStringArray(R.array.consume_item_categories)
                categorizeItemsByAmount(amountRange = consumeAmountRange, items = consumeItems)
            }
        }
    }

    private fun categorizeItemsByAmount(amountRange: IntArray, items: Array<String>) {
        if (amount >= amountRange.last()) {
            showLastItem(name = items.last())
            return
        }

        for (index in 0 until amountRange.lastIndex) {
            if (amount in amountRange[index] until amountRange[index + 1]) {
                if (index == 0) {
                    showFirstCategoryTitle()
                    return
                }

                showOtherCategoryTitle()
                binding.itemName = items[index - 1]
                return
            }
        }
    }

    private fun showLastItem(name: String) {
        showOtherCategoryTitle()
        binding.itemName = name
    }

    private fun showFirstCategoryTitle() {
        binding.llLoadingTitleFirst.isVisible = true
        binding.llLoadingTitleOther.isVisible = false
    }

    private fun showOtherCategoryTitle() {
        binding.llLoadingTitleFirst.isVisible = false
        binding.llLoadingTitleOther.isVisible = true
    }

    private fun switchLottieAnimation() {
        when (feedType) {
            WineyFeedType.SAVE -> {
                binding.lottieConsumeLoading.isVisible = false
                binding.lottieSaveLoading.apply {
                    isVisible = true
                    playAnimation()
                }
            }

            WineyFeedType.CONSUME -> {
                binding.lottieSaveLoading.isVisible = false
                binding.lottieConsumeLoading.apply {
                    isVisible = true
                    playAnimation()
                }
            }
        }

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
