package com.go.sopt.winey.presentation.main.feed.upload.loading

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.ActivityLoadingBinding
import com.go.sopt.winey.presentation.main.MainActivity
import com.go.sopt.winey.util.binding.BindingActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    private val amountString by lazy { intent.extras?.getString(EXTRA_AMOUNT_KEY, "") }
    private val amountRange by lazy { resources.getIntArray(R.array.save_amount_range) }
    private val itemCategories by lazy { resources.getStringArray(R.array.save_item_categories) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classifySaveItemCategory()
        delayMillis()
    }

    private fun classifySaveItemCategory() {
        for (index in amountRange.indices) {
            val amount = amountString?.toLong() ?: return
            if (isLastAmountRange(index)) return

            if (amount >= amountRange[index] && amount < amountRange[index + 1]) {
                if (isFirstAmountRange(index)) return

                binding.apply {
                    saveItem = itemCategories[index]
                    llLoadingTitleFirst.visibility = View.INVISIBLE
                    llLoadingTitleOther.visibility = View.VISIBLE
                }
                return
            }
        }
    }

    private fun isFirstAmountRange(index: Int): Boolean {
        if (index == 0) {
            binding.llLoadingTitleFirst.visibility = View.VISIBLE
            binding.llLoadingTitleOther.visibility = View.INVISIBLE
            return true
        }
        return false
    }

    private fun isLastAmountRange(index: Int): Boolean {
        if (index == amountRange.size - 1) {
            binding.saveItem = itemCategories[index - 1]
            return true
        }
        return false
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
            putExtra(EXTRA_UPLOAD_KEY, true)
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 3000L
        private const val EXTRA_AMOUNT_KEY = "amount"
        private const val EXTRA_UPLOAD_KEY = "upload"
    }
}
