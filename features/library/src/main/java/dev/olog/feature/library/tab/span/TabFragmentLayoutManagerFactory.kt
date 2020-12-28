package dev.olog.feature.library.tab.span

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.library.tab.adapter.TabFragmentAdapter
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.feature.library.tab.model.TabFragmentModel
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager

internal object TabFragmentLayoutManagerFactory {

    private fun createSpanSize(
        category: TabFragmentCategory,
        adapter: ObservableAdapter<TabFragmentModel>,
        requestedSpanSize: Int
    ): AbsSpanSizeLookup {

        return when (category) {
            TabFragmentCategory.PLAYLISTS,
            TabFragmentCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(requestedSpanSize)
            TabFragmentCategory.ALBUMS,
            TabFragmentCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(adapter, requestedSpanSize)
            TabFragmentCategory.ARTISTS,
            TabFragmentCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(adapter, requestedSpanSize)
            TabFragmentCategory.SONGS,
            TabFragmentCategory.PODCASTS -> SongSpanSizeLookup(requestedSpanSize)
            else -> BaseSpanSizeLookup(requestedSpanSize)
        }
    }

    fun create(
        recyclerView: RecyclerView,
        category: TabFragmentCategory,
        adapter: TabFragmentAdapter,
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