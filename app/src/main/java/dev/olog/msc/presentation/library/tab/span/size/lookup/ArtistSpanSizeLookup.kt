package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.configuration
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.model.DisplayableItem

class ArtistSpanSizeLookup(
    context: Context,
    private val isPortrait: Boolean,
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

        var span = if (isPortrait) 3 else 4

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}