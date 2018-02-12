package dev.olog.msc.presentation.detail.scroll.listener

import android.support.v7.widget.RecyclerView
import dev.olog.msc.R
import dev.olog.msc.presentation.detail.DetailFragment
import kotlinx.android.synthetic.main.fragment_detail.view.*
import org.jetbrains.anko.dimen
import kotlin.LazyThreadSafetyMode.NONE

class HeaderVisibilityScrollListener(
        private val fragment: DetailFragment

) : RecyclerView.OnScrollListener() {

    private val context = fragment.context!!
    private val view = fragment.view!!
    private val toolbarHeight by lazy(NONE) { context.dimen(R.dimen.status_bar) + context.dimen(R.dimen.toolbar) }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val child = recyclerView.getChildAt(0)
        val holder = recyclerView.getChildViewHolder(child)

        if (holder.itemViewType == R.layout.item_detail_item_info) {
            val bottom = child.bottom
            val needDarkLayout = bottom - toolbarHeight * 2 < 0

            view.statusBar.isActivated = needDarkLayout
            view.toolbar.isActivated = needDarkLayout
            view.headerText.isActivated = needDarkLayout

            if (needDarkLayout) {
                fragment.setDarkButtons()
            } else {
                fragment.setLightButtons()
            }
        } else {
            view.statusBar.isActivated = true
            view.toolbar.isActivated = true
            view.headerText.isActivated = true
            fragment.setDarkButtons()
        }
    }

}