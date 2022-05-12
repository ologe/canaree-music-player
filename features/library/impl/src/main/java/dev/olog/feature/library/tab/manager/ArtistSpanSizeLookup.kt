package dev.olog.feature.library.tab.manager

import dev.olog.feature.library.R
import dev.olog.platform.adapter.BaseModel
import dev.olog.platform.adapter.ObservableAdapter

class ArtistSpanSizeLookup(
    private val adapter: ObservableAdapter<BaseModel>,
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        when (adapter.getItem(position).type) {
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }
}