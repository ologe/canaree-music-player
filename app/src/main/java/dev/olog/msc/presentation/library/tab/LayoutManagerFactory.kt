package dev.olog.msc.presentation.library.tab

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.presentation.library.tab.span.size.lookup.*
import dev.olog.core.MediaIdCategory
import dev.olog.msc.utils.k.extension.isPortrait

object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: MediaIdCategory, adapter: TabFragmentAdapter): AbsSpanSizeLookup {
        val isPortrait = context.isPortrait

        return when (category){
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(context, isPortrait)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(context, isPortrait, adapter)
            MediaIdCategory.SONGS, MediaIdCategory.PODCASTS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context, isPortrait)
        }
    }

    fun get(context: Context, category: MediaIdCategory, adapter: TabFragmentAdapter): GridLayoutManager {
        val spanSizeLookup = createSpanSize(context, category, adapter)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}