package dev.olog.presentation.tab.layoutmanager

import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.presentation.model.SpanCountController
import dev.olog.presentation.tab.adapter.TabFragmentItem
import dev.olog.shared.compose.component.ComposeListAdapter

class TabSpanSizeLookup(
    private val adapter: ComposeListAdapter<TabFragmentItem>,
    var requestedSpanSize: Int
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        val item = adapter.getItem(position)
        return when (item) {
            is TabFragmentItem.Track,
            is TabFragmentItem.Podcast -> getSpanCount() / requestedSpanSize
            is TabFragmentItem.Album.Scrollable -> getSpanCount() / requestedSpanSize
            is TabFragmentItem.Album.NonScrollable -> getSpanCount() / 3
            is TabFragmentItem.Header,
            is TabFragmentItem.List,
            is TabFragmentItem.Shuffle -> getSpanCount()
        }
    }

    fun getSpanCount() = SpanCountController.SPAN_COUNT

}