package com.android.go.sopt.winey.presentation.main.feed.upload.loading

import android.os.Bundle
import android.os.Handler
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityLoadingBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import timber.log.Timber

class LoadingActivity : BindingActivity<ActivityLoadingBinding>(R.layout.activity_loading) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifySaveItemCategory()
        delayMillis()
    }

    private fun classifySaveItemCategory() {
        val amountString = intent.extras?.getString(EXTRA_AMOUNT_KEY, "") ?: return
        val amount = amountString.toLong()

        val amountRange = resources.getIntArray(R.array.save_amount_range)
        val itemCategories = resources.getStringArray(R.array.save_item_categories)

        for (idx in amountRange.indices) {
            if (idx == amountRange.size - 1) {
                Timber.e("last: ${itemCategories[idx]}")
                binding.saveItem = itemCategories[idx]
                return
            }

            if (amount >= amountRange[idx] && amount < amountRange[idx + 1]) {
                Timber.e(itemCategories[idx])
                binding.saveItem = itemCategories[idx]
                return
            }
        }
    }

    private fun delayMillis() {
        Handler().postDelayed({
            finish()
        }, DELAY_TIME)
    }

    companion object {
        private const val DELAY_TIME = 3000L
        private const val EXTRA_AMOUNT_KEY = "amount"
    }
}