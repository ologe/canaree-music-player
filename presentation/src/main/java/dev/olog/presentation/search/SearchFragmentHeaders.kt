package dev.olog.presentation.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

class SearchFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val recents: List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_recent_header,
            mediaId = MediaId.headerId("recent searches header id"),
            title = context.getString(R.string.search_recent_searches)
        )
    )

    fun songsHeaders(size: Int): DisplayableItem = DisplayableHeader(
        type = R.layout.item_search_header,
        mediaId = MediaId.headerId("songs header id"),
        title = context.getString(R.string.search_songs),
        subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
    )

    fun albumsHeaders(size: Int): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_header,
            mediaId = MediaId.headerId("albums header id"),
            title = context.getString(R.string.search_albums),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_search_list_albums,
            mediaId = MediaId.headerId("albums list id")
        )
    )

    fun artistsHeaders(size: Int): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_header,
            mediaId = MediaId.headerId("artists header id"),
            title = context.getString(R.string.search_artists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_search_list_artists,
            mediaId = MediaId.headerId("artists list id")
        )
    )

    fun foldersHeaders(size: Int): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_header,
            mediaId = MediaId.headerId("folders header id"),
            title = context.getString(R.string.search_folders),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_search_list_folder,
            mediaId = MediaId.headerId("folders list id")
        )
    )

    fun playlistsHeaders(size: Int): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_header,
            mediaId = MediaId.headerId("playlists header id"),
            title = context.getString(R.string.search_playlists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_search_list_playlists,
            mediaId = MediaId.headerId("playlists list id")
        )
    )

    fun genreHeaders(size: Int): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_search_header,
            mediaId = MediaId.headerId("genres header id"),
            title = context.getString(R.string.search_genres),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_search_list_genre,
            mediaId = MediaId.headerId("genres list id")
        )
    )

}