package dev.olog.presentation.tab.layoutmanager

import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.base.BaseModel

class ArtistSpanSizeLookup(
    private val adapter: ObservableAdapter<BaseModel>,
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        when (adapter.getItem(position)!!.type) {
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }
}