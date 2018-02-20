package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adp.AbsAdapter
import dev.olog.msc.presentation.model.DisplayableItem
import org.jetbrains.anko.configuration

class AlbumSpanSizeLookup(
        context: Context,
        private val isPortrait: Boolean,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val isTablet = context.configuration.smallestScreenWidthDp >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        if ((itemType == R.layout.item_tab_header ||
                        itemType == R.layout.item_tab_last_played_artist_horizontal_list ||
                        itemType == R.layout.item_tab_last_played_album_horizontal_list)){
            return spanCount
        }


        if (isTablet){
            return if (isPortrait) spanCount / 3 else spanCount / 4
        }

        if (isPortrait){
            return spanCount / 2
        }
        return spanCount / 4
    }
}