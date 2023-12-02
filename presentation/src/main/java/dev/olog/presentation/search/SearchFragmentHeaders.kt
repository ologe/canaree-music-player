package dev.olog.presentation.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.search.adapter.SearchFragmentItem
import javax.inject.Inject

class SearchFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val recents = SearchFragmentItem.Header(title = context.getString(R.string.search_recent_searches), subtitle = null)

    fun songsHeaders(size: Int) = SearchFragmentItem.Header(
        title = context.getString(R.string.search_songs),
        subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
    )

    fun nestedListHeaders(
        category: MediaIdCategory,
        items: List<SearchFragmentItem.Album>
    ): List<SearchFragmentItem> {
        if (items.isEmpty()) {
            return emptyList()
        }
        val title = when (category) {
            MediaIdCategory.FOLDERS -> R.string.search_folders
            MediaIdCategory.PLAYLISTS -> R.string.search_playlists
            MediaIdCategory.SONGS -> R.string.search_songs
            MediaIdCategory.ALBUMS -> R.string.search_albums
            MediaIdCategory.ARTISTS -> R.string.search_artists
            MediaIdCategory.GENRES -> R.string.search_genres
            MediaIdCategory.PODCASTS_PLAYLIST -> R.string.search_playlists
            else -> error("invalid $category")
        }
        val size = items.size
        return listOf(
            SearchFragmentItem.Header(
                title = context.getString(title),
                subtitle = context.resources.getQuantityString(R.plurals.search_xx_results, size, size)
            ),
            SearchFragmentItem.List(items),
        )
    }

}