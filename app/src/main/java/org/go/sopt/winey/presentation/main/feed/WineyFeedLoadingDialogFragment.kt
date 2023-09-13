package org.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentWineyFeedLoadingDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedLoadingDialogFragment :
    BindingDialogFragment<FragmentWineyFeedLoadingDialogBinding>(
        R.layout.fragment_winey_feed_loading_dialog
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
    }
}
