package dev.olog.feature.library.tab

import android.content.Context
import android.content.res.Resources
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableNestedListPlaceholder
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId
import javax.inject.Inject

internal class TabFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val resources: Resources
        get() = context.resources

    val allPlaylistHeader: DisplayableHeader
        get() = DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all playlist"),
            resources.getString(R.string.tab_all_playlists)
        )

    val autoPlaylistHeader: DisplayableHeader
        get() = DisplayableHeader(
            R.layout.item_tab_header,
            headerId("auto playlist"),
            resources.getString(R.string.tab_auto_playlists)
        )

    val shuffleHeader: DisplayableHeader
        get() = DisplayableHeader(
            R.layout.item_tab_shuffle,
            headerId("tab shuffle"), ""
        )

    val allAlbumsHeader: List<DisplayableHeader>
        get() = listOf(
            DisplayableHeader(
                R.layout.item_tab_header,
                headerId("all albums"),
                resources.getString(R.string.tab_all_albums)
            )
        )

    val allArtistsHeader: List<DisplayableHeader>
        get() = listOf(
            DisplayableHeader(
                R.layout.item_tab_header,
                headerId("all artists"),
                resources.getString(R.string.tab_all_artists)
            )
        )

    val allPodcastAuthorsHeader: List<DisplayableHeader>
        get() = listOf(
            DisplayableHeader(
                R.layout.item_tab_header,
                headerId("all podcast authors"),
                resources.getString(R.string.tab_all_podcast_authors)
            )
        )

    val lastPlayedAlbumHeaders: List<DisplayableItem>
        get() = listOf(
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

    val lastPlayedArtistHeaders: List<DisplayableItem>
        get() = listOf(
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

    val lastPlayedPodcastAuthorsHeaders: List<DisplayableItem>
        get() = listOf(
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

    val recentlyAddedAlbumsHeaders: List<DisplayableItem>
        get() = listOf(
            DisplayableHeader(
                R.layout.item_tab_header, headerId("new albums"),
                resources.getStringArray(R.array.tab_new_items)[0]
            ),
            DisplayableNestedListPlaceholder(
                R.layout.item_tab_new_album_horizontal_list,
                headerId("horiz list new albums")
            )
        )

    val recentlyAddedArtistsHeaders: List<DisplayableItem>
        get() = listOf(
            DisplayableHeader(
                R.layout.item_tab_header, headerId("new artists"),
                resources.getStringArray(R.array.tab_new_items)[1]
            ),
            DisplayableNestedListPlaceholder(
                R.layout.item_tab_new_artist_horizontal_list,
                headerId("horiz list new artists")
            )
        )

    val recentlyAddedPodcastAuthorsHeaders: List<DisplayableItem>
        get() = listOf(
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