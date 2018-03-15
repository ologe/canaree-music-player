package dev.olog.msc.presentation.library.tab.span.size.lookup

import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.model.DisplayableItem

class ArtistSpanSizeLookup(
        private val isPortrait: Boolean,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_download_no_wifi -> return spanCount
        }

        return if (isPortrait) spanCount / 3 else spanCount / 4
    }
}