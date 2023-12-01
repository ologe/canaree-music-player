package dev.olog.presentation.tab.layoutmanager

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.tab.adapter.TabFragmentAdapter
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager

internal object LayoutManagerFactory {

    fun create(
        recyclerView: RecyclerView,
        adapter: TabFragmentAdapter,
        requestedSpanSize: Int
    ): GridLayoutManager {
        val spanSizeLookup = TabSpanSizeLookup(adapter, requestedSpanSize)
        val layoutManager = OverScrollGridLayoutManager(recyclerView, spanSizeLookup.getSpanCount())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}