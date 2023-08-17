package com.android.go.sopt.winey.presentation.main.feed.upload

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityUploadBinding
import com.android.go.sopt.winey.presentation.main.feed.upload.photo.PhotoFragment
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.fragment.WineyDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadActivity : BindingActivity<ActivityUploadBinding>(R.layout.activity_upload) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpDefaultFragment(savedInstanceState)
        registerBackPressedCallback()
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcv_upload, PhotoFragment())
            }
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun showAlertDialog() {
        val dialog = WineyDialogFragment(
            stringOf(R.string.upload_dialog_title),
            stringOf(R.string.upload_dialog_subtitle),
            stringOf(R.string.upload_dialog_negative_button),
            stringOf(R.string.upload_dialog_positive_button),
            handleNegativeButton = {},
            handlePositiveButton = { finish() }
        )
        dialog.show(supportFragmentManager, TAG_UPLOAD_DIALOG)
    }

    companion object {
        private const val TAG_UPLOAD_DIALOG = "UPLOAD_DIALOG"
    }
}
