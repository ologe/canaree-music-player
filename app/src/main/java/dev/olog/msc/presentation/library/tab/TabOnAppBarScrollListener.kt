package dev.olog.msc.presentation.library.tab

import android.support.design.widget.AppBarLayout
import android.widget.FrameLayout
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.dimen
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject

class TabOnAppBarScrollListener @Inject constructor(
        private val fragment: TabFragment

) : AppBarLayout.OnOffsetChangedListener {

    private val marginBottom = fragment.context!!.dimen(R.dimen.tab_fast_scroller_margin_bottom)
    private val marginTop = fragment.context!!.dimen(R.dimen.tab_fast_scroller_margin_top)

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