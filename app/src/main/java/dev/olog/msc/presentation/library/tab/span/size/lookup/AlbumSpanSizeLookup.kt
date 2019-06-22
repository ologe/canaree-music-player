package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.configuration
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.model.DisplayableItem

class AlbumSpanSizeLookup(
    private val context: Context,
    private val adapter: ObservableAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val isPortrait = context.isPortrait
    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.getItem(position)!!.type
        when (itemType) {
            R.layout.item_tab_header,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return spanCount
        }
//
        if (isTablet) {
            val span = if (isPortrait) 4 else 5
            return spanCount / span
        }

        return if (isPortrait) spanCount / 2 else spanCount / 4
    }

}