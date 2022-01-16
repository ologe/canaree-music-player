package dev.olog.feature.library.tab.layout.manager

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager

object TabFragmentLayoutManagerFactory {

    @Suppress("UNCHECKED_CAST")
    fun get(
        recyclerView: RecyclerView,
        category: MediaUri.Category,
        requestedSpanSize: Int
    ): GridLayoutManager {
        val spanSizeLookup = TabFragmentSpanSizeLookup(
            category = category,
            requestedSpanSize = requestedSpanSize
        )
        val layoutManager = OverScrollGridLayoutManager(recyclerView, TabFragmentSpanSizeLookup.SPAN_COUNT)
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}