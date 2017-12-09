package dev.olog.presentation.fragment_tab

import android.content.res.Resources
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabFragmentHeaders @Inject constructor(
        resources: Resources
) {

    val allPlaylistHeader = DisplayableItem(R.layout.item_tab_header,
            "all playlist header", resources.getString(R.string.tab_all_playlists))

    val autoPlaylistHeader = DisplayableItem(R.layout.item_tab_header,
            "auto playlist header", resources.getString(R.string.tab_auto_playlists))

    val shuffleHeader = DisplayableItem(R.layout.item_tab_shuffle,
            "shuffle id","unused")

    val albumHeaders = listOf(
            DisplayableItem(R.layout.item_tab_header, "last played album id", resources.getString(R.string.tab_recent)),
            DisplayableItem(R.layout.item_tab_last_played_album_horizontal_list, "horizontal list last played album id", ""),
            DisplayableItem(R.layout.item_tab_header, "all albums id", resources.getString(R.string.tab_all_albums))
    )

    val artistHeaders = listOf(
            DisplayableItem(R.layout.item_tab_header, "last played artist id", resources.getString(R.string.tab_recent)),
            DisplayableItem(R.layout.item_tab_last_played_artist_horizontal_list, "horizontal list last played artist id", ""),
            DisplayableItem(R.layout.item_tab_header, "all artists id", resources.getString(R.string.tab_all_artists))
    )

}