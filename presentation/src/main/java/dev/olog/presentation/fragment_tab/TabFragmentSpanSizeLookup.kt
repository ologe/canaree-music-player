package dev.olog.presentation.fragment_tab

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation.activity_main.TabViewPagerAdapter.Companion.ALBUM
import dev.olog.presentation.activity_main.TabViewPagerAdapter.Companion.ARTIST
import dev.olog.presentation.activity_main.TabViewPagerAdapter.Companion.PLAYLIST
import dev.olog.presentation.activity_main.TabViewPagerAdapter.Companion.SONG
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.delegates.weakRef
import dev.olog.presentation.utils.extension.isPortrait

class TabFragmentSpanSizeLookup (
        @ActivityContext context: Context,
        private val source: Int,
        adapter: BaseListAdapter<DisplayableItem>

) : GridLayoutManager.SpanSizeLookup() {

    private val adapter by weakRef(adapter)

    companion object {
        const val SPAN_COUNT = 12
    }

    private val isPortrait = context.isPortrait

    private fun getSpan(position: Int): Int{
        if (source == PLAYLIST){
            if (position == 0 || position == 4){
                return SPAN_COUNT
            }
        }
        if (source == ALBUM || source == ARTIST){
            val itemType = adapter?.getItem(position)?.type
            if (itemType != null && (itemType == R.layout.item_tab_header ||
                    itemType == R.layout.item_tab_last_played_artist_horizontal_list ||
                    itemType == R.layout.item_tab_last_played_album_horizontal_list)){
                return SPAN_COUNT
            }
        }

        val isAlbum = source == ALBUM

        return when {
            source == SONG && isPortrait -> SPAN_COUNT
            source == SONG && !isPortrait -> SPAN_COUNT / 2
            (isAlbum) && isPortrait -> SPAN_COUNT / 2
            (isAlbum.not()) && isPortrait -> SPAN_COUNT / 3
            else -> SPAN_COUNT / 4
        }
    }

    override fun getSpanSize(position: Int): Int = getSpan(position)

}