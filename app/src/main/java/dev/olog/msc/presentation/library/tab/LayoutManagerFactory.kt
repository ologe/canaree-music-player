package dev.olog.msc.presentation.library.tab

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.presentation.library.tab.adapters.TabFragmentAdapter
import dev.olog.msc.presentation.library.tab.span.size.lookup.*
import dev.olog.msc.utils.k.extension.isPortrait

internal object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: TabCategory, adapter: TabFragmentAdapter): AbsSpanSizeLookup {
        val isPortrait = context.isPortrait

        return when (category){
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(context, isPortrait)
            TabCategory.ALBUMS,
            TabCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(context, isPortrait, adapter)
            TabCategory.SONGS, TabCategory.PODCASTS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context, isPortrait)
        }
    }

    fun get(context: Context, category: TabCategory, adapter: TabFragmentAdapter): GridLayoutManager {
        val spanSizeLookup = createSpanSize(context, category, adapter)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}