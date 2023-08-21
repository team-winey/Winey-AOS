package com.android.go.sopt.winey.util.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.android.go.sopt.winey.databinding.ItemWineyPopupBinding
import com.android.go.sopt.winey.databinding.LayoutWineyPopupBinding

class WineyPopupMenu(
    private val context: Context,
    private val titles: MutableList<String>,
    private val menuItemClickListener: (View, String, Int) -> Unit
) : PopupWindow(context) {
    init {
        addMenuItemViewToLayout()
    }

    private fun addMenuItemViewToLayout() {
        val inflater = LayoutInflater.from(context)
        val layoutBinding = LayoutWineyPopupBinding.inflate(inflater, null, false)
        val itemBinding = ItemWineyPopupBinding.inflate(inflater, null, false)

        for (i in 0 until titles.size) {
            itemBinding.tvPopupTitle.text = titles[i]
            itemBinding.vPopupSeperator.visibility =
                if (i < titles.size - 1) View.VISIBLE else View.INVISIBLE

            itemBinding.root.setOnClickListener { rootView ->
                menuItemClickListener.invoke(rootView, titles[i], i)
                dismiss()
            }

            layoutBinding.llPopupContainer.addView(itemBinding.root)
        }
    }
}
