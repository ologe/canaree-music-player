package dev.olog.presentation.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.presentation.R
import dev.olog.presentation.search.adapter.SearchItem
import javax.inject.Inject

class SearchFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val recents = SearchItem.Header(context.getString(R.string.search_recent_searches), null)

    fun songsHeaders(size: Int) = SearchItem.Header(
        title = context.getString(R.string.search_songs),
        subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
    )

    fun albumsHeaders(items: List<SearchItem.Album>): List<SearchItem> = listOf(
        SearchItem.Header(
            title = context.getString(R.string.search_albums),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, items.size, items.size)
        ),
        SearchItem.HorizontalList(items)
    )

    fun artistsHeaders(items: List<SearchItem.Album>): List<SearchItem> = listOf(
        SearchItem.Header(
            title = context.getString(R.string.search_artists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, items.size, items.size)
        ),
        SearchItem.HorizontalList(items)
    )

    fun foldersHeaders(items: List<SearchItem.Album>): List<SearchItem> = listOf(
        SearchItem.Header(
            title = context.getString(R.string.search_folders),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, items.size, items.size)
        ),
        SearchItem.HorizontalList(items)
    )

    fun playlistsHeaders(items: List<SearchItem.Album>): List<SearchItem> = listOf(
        SearchItem.Header(
            title = context.getString(R.string.search_playlists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, items.size, items.size)
        ),
        SearchItem.HorizontalList(items)
    )

    fun genreHeaders(items: List<SearchItem.Album>): List<SearchItem> = listOf(
        SearchItem.Header(
            title = context.getString(R.string.search_genres),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, items.size, items.size)
        ),
        SearchItem.HorizontalList(items)
    )

}