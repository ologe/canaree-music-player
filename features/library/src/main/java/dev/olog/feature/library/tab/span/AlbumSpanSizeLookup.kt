package dev.olog.feature.library.tab.span

import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.model.TabFragmentModel

class AlbumSpanSizeLookup(
    private val adapter: ObservableAdapter<TabFragmentModel>,
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        when (adapter.getItem(position).layoutType) {
            R.layout.item_tab_header,
            R.layout.item_tab_recently_added_album_list,
            R.layout.item_tab_last_played_album_list -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }

}