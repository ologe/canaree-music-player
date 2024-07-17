package dev.olog.presentation.tab.layoutmanager

import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.tab.adapter.TabItem

class AlbumSpanSizeLookup(
    private val adapter: ComposeListAdapter<TabItem>,
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int = when (adapter.getItem(position)) {
        is TabItem.Header,
        is TabItem.HorizontalList -> getSpanCount()
        else -> getSpanCount() / requestedSpanSize
    }

}