package dev.olog.presentation.search

import android.content.Context
import dev.olog.presentation.R
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.MediaId
import javax.inject.Inject

@PerFragment
class SearchFragmentHeaders @Inject constructor(
        @ApplicationContext private val context: Context
) {

    val recents = listOf(
        DisplayableItem(
            R.layout.item_search_recent_header, MediaId.headerId("recent searches header id"),
            context.getString(R.string.search_recent_searches)
        )
    )

    fun songsHeaders(size: Int) = DisplayableItem(
        R.layout.item_search_header,
        MediaId.headerId("songs header id"),
        context.getString(R.string.search_songs),
        context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
    )

    fun albumsHeaders(size: Int) = listOf(
        DisplayableItem(
            R.layout.item_search_header,
            MediaId.headerId("albums header id"),
            context.getString(R.string.search_albums),
            context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableItem(
            R.layout.item_search_list_albums,
            MediaId.headerId("albums list id"),
            ""
        )
    )

    fun artistsHeaders(size: Int) = listOf(
        DisplayableItem(
            R.layout.item_search_header,
            MediaId.headerId("artists header id"),
            context.getString(R.string.search_artists),
            context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableItem(
            R.layout.item_search_list_artists,
            MediaId.headerId("artists list id"),
            ""
        )
    )

    fun foldersHeaders(size: Int) = listOf(
        DisplayableItem(
            R.layout.item_search_header,
            MediaId.headerId("folders header id"),
            context.getString(R.string.search_folders),
            context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableItem(
            R.layout.item_search_list_folder,
            MediaId.headerId("folders list id"),
            ""
        )
    )

    fun playlistsHeaders(size: Int) = listOf(
        DisplayableItem(
            R.layout.item_search_header,
            MediaId.headerId("playlists header id"),
            context.getString(R.string.search_playlists),
            context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableItem(
            R.layout.item_search_list_playlists,
            MediaId.headerId("playlists list id"),
            ""
        )
    )

    fun genreHeaders(size: Int) = listOf(
        DisplayableItem(
            R.layout.item_search_header,
            MediaId.headerId("genres header id"),
            context.getString(R.string.search_genres),
            context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        DisplayableItem(
            R.layout.item_search_list_genre,
            MediaId.headerId("genres list id"),
            ""
        )
    )

}