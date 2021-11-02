package dev.olog.presentation.tab.layoutmanager

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.base.BaseModel
import dev.olog.feature.library.TabCategory
import dev.olog.presentation.tab.adapter.TabFragmentAdapter
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager

internal object LayoutManagerFactory {

    private fun createSpanSize(
        category: TabCategory,
        adapter: ObservableAdapter<BaseModel>,
        requestedSpanSize: Int
    ): AbsSpanSizeLookup {

        return when (category) {
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(requestedSpanSize)
            TabCategory.ALBUMS,
            TabCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(adapter, requestedSpanSize)
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(adapter, requestedSpanSize)
            TabCategory.SONGS, TabCategory.PODCASTS -> SongSpanSizeLookup(requestedSpanSize)
            else -> BaseSpanSizeLookup(requestedSpanSize)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(
        recyclerView: RecyclerView,
        category: TabCategory,
        adapter: TabFragmentAdapter,
        requestedSpanSize: Int
    ): GridLayoutManager {
        val spanSizeLookup = createSpanSize(
            category,
            adapter as ObservableAdapter<BaseModel>,
            requestedSpanSize
        )
        val layoutManager = OverScrollGridLayoutManager(recyclerView, spanSizeLookup.getSpanCount())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}