package dev.olog.presentation.tab

import android.content.res.Resources
import dev.olog.presentation.R
import dev.olog.presentation.tab.adapter.TabItem
import javax.inject.Inject

class TabFragmentHeaders @Inject constructor(
    private val resources: Resources
) {

    val allPlaylistHeader = TabItem.Header(resources.getString(R.string.tab_all_playlists))

    val autoPlaylistHeader = TabItem.Header(resources.getString(R.string.tab_auto_playlists))

    val shuffleHeader = TabItem.Shuffle

    val allAlbumsHeader = TabItem.Header(resources.getString(R.string.tab_all_albums))

    val allArtistsHeader = TabItem.Header(resources.getString(R.string.tab_all_artists))

    fun lastPlayedAlbumHeaders(items: List<TabItem.Album>) = listOf(
        TabItem.Header(resources.getString(R.string.tab_recent_played)),
        TabItem.HorizontalList(items)
    )

    fun lastPlayedArtistHeaders(items: List<TabItem.Album>) = listOf(
        TabItem.Header(resources.getString(R.string.tab_recent_played)),
        TabItem.HorizontalList(items)
    )

    fun recentlyAddedAlbumsHeaders(items: List<TabItem.Album>) = listOf(
        TabItem.Header(resources.getStringArray(R.array.tab_new_items)[0]),
        TabItem.HorizontalList(items)
    )

    fun recentlyAddedArtistsHeaders(items: List<TabItem.Album>) = listOf(
        TabItem.Header(resources.getStringArray(R.array.tab_new_items)[1]),
        TabItem.HorizontalList(items)
    )

}