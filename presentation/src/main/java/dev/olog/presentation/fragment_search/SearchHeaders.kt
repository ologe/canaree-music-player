package dev.olog.presentation.fragment_search

import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

class SearchHeaders @Inject constructor(
        @ApplicationContext private val context: Context
) {

    val songs = listOf(DisplayableItem(R.layout.item_header, "songs header id",
            context.getString(R.string.search_songs)))

    val albums = listOf(
            DisplayableItem(R.layout.item_header, "albums header id",
                    context.getString(R.string.search_albums)),
            DisplayableItem(R.layout.item_search_albums_horizontal_list, "albums list id", "")
    )

    val artists = listOf(
            DisplayableItem(R.layout.item_header, "artists header id",
                    context.getString(R.string.search_artists)),
            DisplayableItem(R.layout.item_search_artists_horizontal_list, "artists list id", "")
    )

}