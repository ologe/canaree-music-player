package dev.olog.msc.presentation.library.tab

import android.support.design.widget.AppBarLayout
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.fragment_tab.view.*
import org.jetbrains.anko.dip
import javax.inject.Inject

class TabOnAppBarScrollListener @Inject constructor(
        private val fragment: TabFragment

) : AppBarLayout.OnOffsetChangedListener {

    private val marginBottom = fragment.context!!.dip(96)
    private val marginTop = fragment.context!!.dip(32)

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val view = fragment.view
        view?.let { it.sidebar?.let { sidebar ->
            if (!sidebar.isInLayout){
                val layoutParams = sidebar.layoutParams as FrameLayout.LayoutParams
                layoutParams.bottomMargin = marginBottom - Math.abs(verticalOffset)
                layoutParams.topMargin = marginTop + Math.abs(verticalOffset)
                sidebar.layoutParams = layoutParams
            }
        } }
    }
}