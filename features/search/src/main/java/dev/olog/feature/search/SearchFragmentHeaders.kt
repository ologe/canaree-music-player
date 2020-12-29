package dev.olog.feature.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.search.model.SearchFragmentModel
import javax.inject.Inject

internal class SearchFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val recent: SearchFragmentModel = SearchFragmentModel.RecentHeader

    fun songsHeaders(size: Int): SearchFragmentModel = SearchFragmentModel.Header(
        title = context.getString(R.string.search_songs),
        subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
    )

    fun albumsHeaders(size: Int): List<SearchFragmentModel> = listOf(
        SearchFragmentModel.Header(
            title = context.getString(R.string.search_albums),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        SearchFragmentModel.AlbumsList
    )

    fun artistsHeaders(size: Int): List<SearchFragmentModel> = listOf(
        SearchFragmentModel.Header(
            title = context.getString(R.string.search_artists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        SearchFragmentModel.ArtistsList
    )

    fun foldersHeaders(size: Int): List<SearchFragmentModel> = listOf(
        SearchFragmentModel.Header(
            title = context.getString(R.string.search_folders),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        SearchFragmentModel.FoldersList
    )

    fun playlistsHeaders(size: Int): List<SearchFragmentModel> = listOf(
        SearchFragmentModel.Header(
            title = context.getString(R.string.search_playlists),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        SearchFragmentModel.PlaylistList
    )

    fun genreHeaders(size: Int): List<SearchFragmentModel> = listOf(
        SearchFragmentModel.Header(
            title = context.getString(R.string.search_genres),
            subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
        ),
        SearchFragmentModel.GenreList
    )

}