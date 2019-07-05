package dev.olog.presentation.tab.layoutmanager

import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.extensions.configuration

class ArtistSpanSizeLookup(
        context: Context,
        private val adapter: ObservableAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.getItem(position)!!.type
        when (itemType) {
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return spanCount
        }

        var span = 3

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}