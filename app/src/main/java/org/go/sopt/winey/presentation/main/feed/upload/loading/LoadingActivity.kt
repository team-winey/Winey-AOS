package org.go.sopt.winey.presentation.main.feed.upload.loading

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityLoadingBinding
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.util.binding.BindingActivity

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    private val amount by lazy { intent.extras?.getString(KEY_SAVE_AMOUNT, "") }
    private val amountRange by lazy { resources.getIntArray(R.array.save_amount_range) }
    private val itemCategories by lazy { resources.getStringArray(R.array.save_item_categories) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classifySaveItemCategory()
        delayMillis()
    }

    private fun classifySaveItemCategory() {
        for (index in amountRange.indices) {
            val amount = amount?.toLong() ?: return
            if (isLastAmountRange(index)) return

            if (amount >= amountRange[index] && amount < amountRange[index + 1]) {
                if (isFirstAmountRange(index)) return

                binding.apply {
                    showOtherCategoryLoading()
                    saveItem = itemCategories[index - 1]
                }
                return
            }
        }
    }

    private fun isFirstAmountRange(index: Int): Boolean {
        if (index == 0) {
            showFirstCategoryLoading()
            return true
        }
        return false
    }

    private fun isLastAmountRange(index: Int): Boolean {
        if (index == amountRange.size - 1) {
            showOtherCategoryLoading()
            binding.saveItem = itemCategories[index - 1]
            return true
        }
        return false
    }

    private fun showFirstCategoryLoading() {
        binding.llLoadingTitleFirst.visibility = View.VISIBLE
        binding.llLoadingTitleOther.visibility = View.INVISIBLE
    }

    private fun showOtherCategoryLoading() {
        binding.llLoadingTitleFirst.visibility = View.INVISIBLE
        binding.llLoadingTitleOther.visibility = View.VISIBLE
    }

    private fun delayMillis() {
        lifecycleScope.launch {
            delay(DELAY_TIME)
            navigateToMainScreen()
        }
    }

    private fun navigateToMainScreen() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(KEY_FEED_UPLOAD, true)
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 3000L
        private const val KEY_SAVE_AMOUNT = "amount"
        private const val KEY_FEED_UPLOAD = "upload"
    }
}
