package org.go.sopt.winey.presentation.main.feed

import android.os.Bundle
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentWineyfeedLoadingDialogBinding
import org.go.sopt.winey.util.binding.BindingDialogFragment

class WineyFeedLoadingDialogFragment :
    BindingDialogFragment<FragmentWineyfeedLoadingDialogBinding>(R.layout.fragment_wineyfeed_loading_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
    }
}
