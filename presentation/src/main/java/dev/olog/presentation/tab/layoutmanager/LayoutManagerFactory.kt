package dev.olog.presentation.tab.layoutmanager

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.tab.adapter.TabItem
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager

internal object LayoutManagerFactory {

    private fun createSpanSize(
        category: MediaIdCategory,
        adapter: ComposeListAdapter<TabItem>,
        requestedSpanSize: Int
    ): AbsSpanSizeLookup {

        return when (category) {
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(requestedSpanSize)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(adapter, requestedSpanSize)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(adapter, requestedSpanSize)
            MediaIdCategory.SONGS, MediaIdCategory.PODCASTS -> SongSpanSizeLookup(requestedSpanSize)
            else -> BaseSpanSizeLookup(requestedSpanSize)
        }
    }

    fun get(
        recyclerView: RecyclerView,
        category: MediaIdCategory,
        adapter: ComposeListAdapter<TabItem>,
        requestedSpanSize: Int
    ): GridLayoutManager {
        val spanSizeLookup = createSpanSize(
            category = category,
            adapter = adapter,
            requestedSpanSize = requestedSpanSize
        )
        val layoutManager = OverScrollGridLayoutManager(recyclerView, spanSizeLookup.getSpanCount())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}