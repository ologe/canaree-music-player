package dev.olog.presentation.tab

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

class TabFragmentHeaders @Inject constructor(
        resources: Resources
) {

    val allPlaylistHeader = DisplayableHeader(
        R.layout.item_tab_header,
        MediaId.headerId("all playlist"),
        resources.getString(R.string.tab_all_playlists)
    )

    val autoPlaylistHeader = DisplayableHeader(
        R.layout.item_tab_header,
        MediaId.headerId("auto playlist"),
        resources.getString(R.string.tab_auto_playlists)
    )

    val shuffleHeader = DisplayableHeader(
        R.layout.item_tab_shuffle,
        MediaId.headerId("tab shuffle"), ""
    )

    val allAlbumsHeader = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            MediaId.headerId("all albums"),
            resources.getString(R.string.tab_all_albums)
        )
    )

    val allArtistsHeader = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            MediaId.headerId("all artists"),
            resources.getString(R.string.tab_all_artists)
        )
    )

    val lastPlayedAlbumHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            MediaId.headerId("recent albums"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_album_horizontal_list,
            MediaId.headerId("horiz list album")
        )
    )

    val lastPlayedArtistHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            MediaId.headerId("recent artists"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_artist_horizontal_list,
            MediaId.headerId("horiz list artist")
        )
    )

    val recentlyAddedAlbumsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, MediaId.headerId("new albums"),
            resources.getStringArray(R.array.tab_new_items)[0]
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_album_horizontal_list,
            MediaId.headerId("horiz list new albums")
        )
    )

    val recentlyAddedArtistsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, MediaId.headerId("new artists"),
            resources.getStringArray(R.array.tab_new_items)[1]
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_artist_horizontal_list,
            MediaId.headerId("horiz list new artists")
        )
    )

}