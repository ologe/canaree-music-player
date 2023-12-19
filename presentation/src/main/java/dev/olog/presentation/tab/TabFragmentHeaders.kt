package dev.olog.presentation.tab

import android.content.res.Resources
import dev.olog.presentation.R
import javax.inject.Inject

class TabFragmentHeaders @Inject constructor(
    private val resources: Resources
) {

    val allPlaylistHeader = TabListItem.Header(resources.getString(R.string.tab_all_playlists))

    val autoPlaylistHeader = TabListItem.Header(resources.getString(R.string.tab_auto_playlists))

    val allAlbumsHeader = TabListItem.Header(resources.getString(R.string.tab_all_albums))

    val allArtistsHeader = TabListItem.Header(resources.getString(R.string.tab_all_artists))

    fun recentlyPlayed(items: List<TabListItem.Album>) = listOf(
        TabListItem.Header(resources.getString(R.string.tab_recent_played)),
        TabListItem.List(items),
    )

    fun recentlyAddedAlbums(items: List<TabListItem.Album>) = listOf(
        TabListItem.Header(resources.getStringArray(R.array.tab_new_items)[0]),
        TabListItem.List(items),
    )

    fun recentlyAddedArtist(items: List<TabListItem.Album>) = listOf(
        TabListItem.Header(resources.getStringArray(R.array.tab_new_items)[1]),
        TabListItem.List(items),
    )

}