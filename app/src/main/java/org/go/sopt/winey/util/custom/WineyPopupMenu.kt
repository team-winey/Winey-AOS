package org.go.sopt.winey.util.custom

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ItemWineyPopupBinding
import org.go.sopt.winey.databinding.LayoutWineyPopupBinding
import org.go.sopt.winey.util.context.drawableOf

class WineyPopupMenu(
    private val context: Context,
    private val titles: List<String>,
    private val menuItemClickListener: (View, String, Int) -> Unit
) : PopupWindow(context) {
    init {
        isOutsideTouchable = true
        isTouchable = true
        inflateMenuItemsToLayout()
        setUpContentView()
    }

    private fun inflateMenuItemsToLayout() {
        val inflater = LayoutInflater.from(context)
        val layoutBinding = LayoutWineyPopupBinding.inflate(inflater, null, false)
        contentView = layoutBinding.root

        for (i in titles.indices) {
            val itemBinding = ItemWineyPopupBinding.inflate(inflater, null, false)
            itemBinding.tvPopupTitle.text = titles[i]
            itemBinding.vPopupSeperator.visibility =
                if (i < titles.size - 1) View.VISIBLE else View.INVISIBLE

            val menuItemView = itemBinding.root
            menuItemView.setOnClickListener { view ->
                menuItemClickListener.invoke(view, titles[i], i)
                dismiss()
            }
            layoutBinding.llPopupContainer.addView(menuItemView)
        }
    }

    private fun setUpContentView() {
        width = getDp(context, POPUP_MENU_WIDTH)
        contentView.measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        height = contentView.measuredHeight
        setBackgroundDrawable(context.drawableOf(R.drawable.shape_transparent_fill_5_rect))
    }

    private fun getDp(context: Context, value: Float): Int {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm).toInt()
    }

    fun showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, -POPUP_MENU_OFFSET, -POPUP_MENU_OFFSET, Gravity.END)
    }

    companion object {
        private const val POPUP_MENU_WIDTH = 120f
        private const val POPUP_MENU_OFFSET = 65
    }
}
