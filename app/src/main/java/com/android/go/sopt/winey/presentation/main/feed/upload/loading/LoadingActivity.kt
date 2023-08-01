package com.android.go.sopt.winey.presentation.main.feed.upload.loading

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityLoadingBinding
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.util.binding.BindingActivity

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
                binding.saveItem = itemCategories[idx]
                return
            }

            if (amount >= amountRange[idx] && amount < amountRange[idx + 1]) {
                binding.saveItem = itemCategories[idx]
                return
            }
        }
    }

    private fun delayMillis() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateMainScreen()
        }, DELAY_TIME)
    }

    private fun navigateMainScreen() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 3000L
        private const val EXTRA_AMOUNT_KEY = "amount"
    }
}
