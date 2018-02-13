package dev.olog.msc.presentation.search

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

@PerFragment
class SearchFragmentHeaders @Inject constructor(
        @ApplicationContext private val context: Context
) {

    val recents = listOf(DisplayableItem(R.layout.item_search_recent_header, MediaId.headerId("recent searches header id"),
            context.getString(R.string.search_recent_searches)))

    fun songsHeaders(size: Int) = DisplayableItem(R.layout.item_search_header, MediaId.headerId("songs header id"),
            context.getString(R.string.search_songs), context.resources.getQuantityString(R.plurals.search_xx_results, size, size))

    fun albumsHeaders(size: Int) = mutableListOf(
            DisplayableItem(R.layout.item_search_header, MediaId.headerId("albums header id"),
                    context.getString(R.string.search_albums), context.resources.getQuantityString(R.plurals.search_xx_results, size, size)),
            DisplayableItem(R.layout.item_search_albums_horizontal_list, MediaId.headerId("albums list id"), "")
    )

    fun artistsHeaders(size: Int) = mutableListOf(
            DisplayableItem(R.layout.item_search_header, MediaId.headerId("artists header id"),
                    context.getString(R.string.search_artists), context.resources.getQuantityString(R.plurals.search_xx_results, size, size)),
            DisplayableItem(R.layout.item_search_artists_horizontal_list, MediaId.headerId("artists list id"), "")
    )

}