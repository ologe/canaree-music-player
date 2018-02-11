package dev.olog.msc.presentation.library.tab

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import dev.olog.msc.R
import dev.olog.msc.dagger.ActivityContext
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.isPortrait
import org.jetbrains.anko.configuration
import java.lang.ref.WeakReference

private const val SPAN_COUNT = 12

class TabSpanSpanSizeLookupFactory(
        @ActivityContext private val context: Context,
        private val category: MediaIdCategory,
        adapter: BaseListAdapter<DisplayableItem>
){

    private val adapter = WeakReference<BaseListAdapter<DisplayableItem>>(adapter)
    private val isPortrait = context.isPortrait

    fun get() : GridLayoutManager.SpanSizeLookup {
        return when (category){
            MediaIdCategory.PLAYLIST -> PlaylistSpanSizeLookup(isPortrait)
            MediaIdCategory.ALBUM -> AlbumSpanSizeLookup(context, isPortrait, adapter)
            MediaIdCategory.ARTIST -> ArtistSpanSizeLookup(isPortrait, adapter)
            MediaIdCategory.SONGS -> SongSpanSizeLookup(isPortrait)
            else -> BaseSpanSizeLookup(isPortrait)
        }
    }

    fun getSpanSize() = SPAN_COUNT

}

class PlaylistSpanSizeLookup(
        private val isPortrait: Boolean

) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        if (position == 0 || position == 4){
            return SPAN_COUNT
        }
        return if (isPortrait){
            SPAN_COUNT / 3
        } else {
            SPAN_COUNT / 4
        }
    }
}

class AlbumSpanSizeLookup(
        private val context: Context,
        private val isPortrait: Boolean,
        private val adapter: WeakReference<BaseListAdapter<DisplayableItem>>

) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        adapter.get()?.let {
            val itemType = it.getItemAt(position).type
            if ((itemType == R.layout.item_tab_header ||
                    itemType == R.layout.item_tab_last_played_artist_horizontal_list ||
                    itemType == R.layout.item_tab_last_played_album_horizontal_list)){
                return SPAN_COUNT
            }
        }

        val smallest = context.configuration.smallestScreenWidthDp
        if (smallest >= 600){ // for tablets
            return if (isPortrait) SPAN_COUNT / 3 else SPAN_COUNT / 4
        }

        if (isPortrait){
            return SPAN_COUNT / 2
        }
        return SPAN_COUNT / 4
    }
}

class ArtistSpanSizeLookup(
        private val isPortrait: Boolean,
        private val adapter: WeakReference<BaseListAdapter<DisplayableItem>>

) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        adapter.get()?.let {
            val itemType = it.getItemAt(position).type
            if ((itemType == R.layout.item_tab_header ||
                    itemType == R.layout.item_tab_last_played_artist_horizontal_list ||
                    itemType == R.layout.item_tab_last_played_album_horizontal_list)){
                return SPAN_COUNT
            }
        }

        if (isPortrait){
            return SPAN_COUNT / 3
        }

        return SPAN_COUNT / 4
    }
}

class SongSpanSizeLookup(
        private val isPortrait: Boolean
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return when {
            isPortrait -> SPAN_COUNT
            position == 0 && !isPortrait -> SPAN_COUNT
            else -> SPAN_COUNT / 2
        }
    }
}

class BaseSpanSizeLookup(
        private val isPortrait: Boolean
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return if (isPortrait) {
            SPAN_COUNT / 3
        } else {
            SPAN_COUNT / 4
        }
    }
}