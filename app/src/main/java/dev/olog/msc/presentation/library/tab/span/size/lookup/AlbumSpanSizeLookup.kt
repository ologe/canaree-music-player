package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.isOneHanded

class AlbumSpanSizeLookup(
        private val context: Context,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private var oneHanded : Int = 2
    private var twoHanded : Int = 4

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return spanCount
        }

        if (context.isOneHanded()){
            return spanCount / oneHanded
        }

        return spanCount / twoHanded
//
//        if (isTablet){
//            val span = if (isPortrait) 4 else 5
//            return spanCount / span
//        }
//
//        return if(isPortrait) spanCount / 2 else spanCount / 4
    }

    override fun updateSpan(oneHanded: Int, twoHanded: Int) {
        this.oneHanded = oneHanded
        this.twoHanded = twoHanded
    }

}