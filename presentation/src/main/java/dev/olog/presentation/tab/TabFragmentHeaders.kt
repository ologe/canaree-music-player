package dev.olog.presentation.tab

import android.content.res.Resources
import dev.olog.presentation.R
import dev.olog.presentation.tab.adapter.TabFragmentItem
import javax.inject.Inject

class TabFragmentHeaders @Inject constructor(
    private val resources: Resources
) {

    val allPlaylistHeader = TabFragmentItem.Header(resources.getString(R.string.tab_all_playlists))

    val autoPlaylistHeader = TabFragmentItem.Header(resources.getString(R.string.tab_auto_playlists))

    val allAlbumsHeader = TabFragmentItem.Header(resources.getString(R.string.tab_all_albums))

    val allArtistsHeader = TabFragmentItem.Header(resources.getString(R.string.tab_all_artists))

    fun recentlyPlayed(items: List<TabFragmentItem.Album>) = listOf(
        TabFragmentItem.Header(resources.getString(R.string.tab_recent_played)),
        TabFragmentItem.List(items),
    )

    fun recentlyAddedAlbums(items: List<TabFragmentItem.Album>) = listOf(
        TabFragmentItem.Header(resources.getStringArray(R.array.tab_new_items)[0]),
        TabFragmentItem.List(items),
    )

    fun recentlyAddedArtist(items: List<TabFragmentItem.Album>) = listOf(
        TabFragmentItem.Header(resources.getStringArray(R.array.tab_new_items)[1]),
        TabFragmentItem.List(items),
    )

}