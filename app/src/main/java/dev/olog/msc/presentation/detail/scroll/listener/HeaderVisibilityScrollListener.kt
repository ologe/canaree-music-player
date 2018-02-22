package dev.olog.msc.presentation.detail.scroll.listener

import android.support.v7.widget.RecyclerView
import dev.olog.msc.R
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.utils.k.extension.toggleVisibility
import kotlinx.android.synthetic.main.fragment_detail.view.*
import org.jetbrains.anko.dimen

class HeaderVisibilityScrollListener(
        private val fragment: DetailFragment

) : RecyclerView.OnScrollListener() {

    private val context = fragment.context!!
    private val toolbarHeight = context.dimen(R.dimen.status_bar) + context.dimen(R.dimen.toolbar)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val child = recyclerView.getChildAt(0)
        val holder = recyclerView.getChildViewHolder(child)

        val view = fragment.view!!

        if (holder.itemViewType == R.layout.item_detail_item_image) {
            val bottom = child.bottom
            val needDarkLayout = bottom - toolbarHeight * 2 < 0

            view.statusBar.toggleVisibility(needDarkLayout)
            view.toolbar.toggleVisibility(needDarkLayout)
            view.headerText.toggleVisibility(needDarkLayout)

            fragment.hasLightStatusBarColor = needDarkLayout

        } else {
            view.statusBar.toggleVisibility(true)
            view.toolbar.toggleVisibility(true)
            view.headerText.toggleVisibility(true)

            fragment.hasLightStatusBarColor = true
        }
    }

}