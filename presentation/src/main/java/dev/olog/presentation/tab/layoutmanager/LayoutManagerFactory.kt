package dev.olog.presentation.tab.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.model.BaseModel
import dev.olog.presentation.tab.TabCategory
import dev.olog.presentation.tab.adapter.TabFragmentAdapter

internal object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: TabCategory, adapter: ObservableAdapter<BaseModel>): AbsSpanSizeLookup {

        return when (category) {
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(context)
            TabCategory.ALBUMS,
            TabCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(context, adapter)
            TabCategory.SONGS, TabCategory.PODCASTS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(context: Context, category: TabCategory, adapter: TabFragmentAdapter): GridLayoutManager {
        val spanSizeLookup = createSpanSize(context, category, adapter as ObservableAdapter<BaseModel>)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}