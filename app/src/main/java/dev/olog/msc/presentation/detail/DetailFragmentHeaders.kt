package dev.olog.msc.presentation.detail

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
        @ApplicationContext private val context: Context,
        mediaId: MediaId
) {

    companion object {
        val RECENTLY_ADDED_ID = MediaId.headerId("recent header with see all")
    }

    val mostPlayed = listOf(
            DisplayableItem(R.layout.item_detail_header, MediaId.headerId("most played header"), context.getString(R.string.detail_most_played)),
            DisplayableItem(R.layout.item_detail_most_played_list, MediaId.headerId("most played horiz list"), "")
    )

    fun recent(listSize: Int) = listOf(
            DisplayableItem(R.layout.item_detail_header, MediaId.headerId("recent header"), context.getString(R.string.detail_recently_added), trackNumber = context.resources.getQuantityString(R.plurals.xx_new_songs, listSize, listSize)),
            DisplayableItem(R.layout.item_detail_recently_added_list, MediaId.headerId("recent horiz list"), "")
    )

    fun recentWithSeeAll(listSize: Int) = listOf(
            DisplayableItem(R.layout.item_detail_header, RECENTLY_ADDED_ID, context.getString(R.string.detail_recently_added), context.getString(R.string.detail_see_all), trackNumber = context.resources.getQuantityString(R.plurals.xx_new_songs, listSize, listSize)),
            DisplayableItem(R.layout.item_detail_recently_added_list, MediaId.headerId("recent horiz list"), "")
    )

    val albums : DisplayableItem = DisplayableItem(R.layout.item_detail_header, MediaId.headerId("detail albums"),
            context.resources.getStringArray(R.array.detail_album_header)[mediaId.source])

    val shuffle = DisplayableItem(R.layout.item_detail_shuffle, MediaId.headerId("detail shuffle"), "")

    val songs = listOf(
            DisplayableItem(R.layout.item_detail_header_all_song, MediaId.headerId("detail songs header"), context.getString(R.string.detail_songs)),
            shuffle
    )

    val no_songs = DisplayableItem(R.layout.item_detail_empty_state, MediaId.headerId("detail empty state"), "")

}