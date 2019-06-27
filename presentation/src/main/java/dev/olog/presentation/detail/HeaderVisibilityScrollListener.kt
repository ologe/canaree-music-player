package dev.olog.presentation.detail

import android.view.View
import dev.olog.presentation.R
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class HeaderVisibilityScrollListener(
        private val fragment: DetailFragment

) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    private val toolbarHeight by lazyFast {
        val statusBarHeight = fragment.view!!.statusBar.height
        statusBarHeight + fragment.ctx.dimen(R.dimen.toolbar)
    }

    private var textWrapper : View? = null

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        val child = recyclerView.getChildAt(0)
        val holder = recyclerView.getChildViewHolder(child)

        val view = fragment.view!!

        if (holder.itemViewType == R.layout.item_detail_item_image) {
            if (textWrapper == null){
                textWrapper = child.findViewById(R.id.textWrapper)
            }
            val bottom = child.bottom - textWrapper!!.height
            val needDarkLayout = bottom - toolbarHeight < 0

            view.statusBar.toggleVisibility(needDarkLayout, false)
            toggleToolbarBackground(view.toolbar, needDarkLayout)
            view.headerText.toggleVisibility(needDarkLayout, false)

            fragment.hasLightStatusBarColor = needDarkLayout

        } else {
            view.statusBar.toggleVisibility(true, false)
            toggleToolbarBackground(view.toolbar, true)
            view.headerText.toggleVisibility(true, false)

            fragment.hasLightStatusBarColor = true
        }
    }

    private fun toggleToolbarBackground(toolbar: View, show: Boolean){
        if (show && toolbar.background == null){
            toolbar.setBackgroundColor(toolbar.context.colorSurface())
        } else if (!show && toolbar.background != null){
            toolbar.background = null
        }
    }

}