package dev.olog.presentation.fragment_search

import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

@PerFragment
class SearchFragmentHeaders @Inject constructor(
        @ApplicationContext private val context: Context
) {

    val recents = listOf(DisplayableItem(R.layout.item_search_recent_header, "recent searches header id",
            context.getString(R.string.search_recent_searches)))

    fun songsHeaders(size: Int) = listOf(DisplayableItem(R.layout.item_search_header, "songs header id",
            context.getString(R.string.search_songs), context.resources.getQuantityString(R.plurals.search_xx_results, size, size)))

    fun albumsHeaders(size: Int) = listOf(
            DisplayableItem(R.layout.item_search_header, "albums header id",
                    context.getString(R.string.search_albums), context.resources.getQuantityString(R.plurals.search_xx_results, size, size)),
            DisplayableItem(R.layout.item_search_albums_horizontal_list, "albums list id", "")
    )

    fun artistsHeaders(size: Int) = listOf(
            DisplayableItem(R.layout.item_search_header, "artists header id",
                    context.getString(R.string.search_artists), context.resources.getQuantityString(R.plurals.search_xx_results, size, size)),
            DisplayableItem(R.layout.item_search_artists_horizontal_list, "artists list id", "")
    )

}