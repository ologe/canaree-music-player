package dev.olog.presentation.tab.layoutmanager

import dev.olog.presentation.R
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.BaseModel

class AlbumSpanSizeLookup(
    private val adapter: ObservableAdapter<BaseModel>,
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        when (adapter.getItem(position)!!.type) {
            R.layout.item_tab_header,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }

}