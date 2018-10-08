package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.configuration
import dev.olog.msc.utils.k.extension.isPortrait

class AlbumSpanSizeLookup(
        private val context: Context,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val isPortrait = context.isPortrait
    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return spanCount
        }
//
        if (isTablet){
            val span = if (isPortrait) 4 else 5
            return spanCount / span
        }

        return if(isPortrait) spanCount / 2 else spanCount / 4
    }

}