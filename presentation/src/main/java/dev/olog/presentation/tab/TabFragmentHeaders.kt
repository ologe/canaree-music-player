package dev.olog.presentation.tab

import android.content.res.Resources
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId
import dev.olog.presentation.R
import dev.olog.presentation.dagger.PerFragment
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

@PerFragment
class TabFragmentHeaders @Inject constructor(
        resources: Resources
) {

    val allPlaylistHeader =
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all playlist"),
            resources.getString(R.string.tab_all_playlists)
        )

    val autoPlaylistHeader =
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("auto playlist"),
            resources.getString(R.string.tab_auto_playlists)
        )

    val shuffleHeader =
        DisplayableHeader(
            R.layout.item_tab_shuffle,
            headerId("tab shuffle"), ""
        )

    val allAlbumsHeader = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all albums"),
            resources.getString(R.string.tab_all_albums)
        )
    )

    val allArtistsHeader = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all artists"),
            resources.getString(R.string.tab_all_artists)
        )
    )

    val allPodcastAuthorsHeader = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all podcast authors"),
            resources.getString(R.string.tab_all_podcast_authors)
        )
    )

    val lastPlayedAlbumHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("recent albums"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_album_horizontal_list,
            headerId("horiz list album")
        )
    )

    val lastPlayedArtistHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("recent artists"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_artist_horizontal_list,
            headerId("horiz list artist")
        )
    )

    val lastPlayedPodcastAuthorsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            headerId("recent podcast authors"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_artist_horizontal_list,
            headerId("horiz list podcast authors")
        )
    )

    val recentlyAddedAlbumsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, headerId("new albums"),
            resources.getStringArray(R.array.tab_new_items)[0]
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_album_horizontal_list,
            headerId("horiz list new albums")
        )
    )

    val recentlyAddedArtistsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, headerId("new artists"),
            resources.getStringArray(R.array.tab_new_items)[1]
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_artist_horizontal_list,
            headerId("horiz list new artists")
        )
    )

    val recentlyAddedPodcastAuthorsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, headerId("new authors"),
            resources.getString(R.string.tab_new_podcast_authors)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_artist_horizontal_list,
            headerId("horiz list new authors")
        )
    )

}