package dev.olog.msc.presentation.detail

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaId: MediaId
) {

    companion object {
        val RECENTLY_ADDED_SEE_ALL = MediaId.headerId("recently added header see all")
        val RELATED_ARTISTS_SEE_ALL = MediaId.headerId("related artist header see all")
    }

    val mostPlayed = listOf(
            DisplayableItem(R.layout.item_detail_header, MediaId.headerId("most played header"), context.getString(R.string.detail_most_played)),
            DisplayableItem(R.layout.item_detail_most_played_list, MediaId.headerId("most played horiz list"), "")
    )

    val relatedArtists = listOf(
            DisplayableItem(R.layout.item_detail_header, MediaId.headerId("related artist header"), context.getString(R.string.detail_related_artists)),
            DisplayableItem(R.layout.item_detail_related_artists_list, MediaId.headerId("related artist list"), "")
    )

    val relatedArtistsWithSeeAll = listOf(
            DisplayableItem(R.layout.item_detail_header, MediaId.headerId("related artist header"), context.getString(R.string.detail_related_artists)),
            DisplayableItem(R.layout.item_detail_related_artists_list, MediaId.headerId("related artist list"), ""),
            DisplayableItem(R.layout.item_detail_see_all, RELATED_ARTISTS_SEE_ALL, "")
    )

    fun recent(listSize: Int) = listOf(
            DisplayableItem(R.layout.item_detail_header_recently_added, MediaId.headerId("recent header"), context.getString(R.string.detail_recently_added), trackNumber = context.resources.getQuantityString(R.plurals.xx_new_songs, listSize, listSize)),
            DisplayableItem(R.layout.item_detail_recently_added_list, MediaId.headerId("recent horiz list"), "")
    )

    /*
        isExplicit is used to show/hide arrow in the layout
     */
    fun recentWithSeeAll(listSize: Int) = listOf(
            DisplayableItem(R.layout.item_detail_header_recently_added, MediaId.headerId("recent header"), context.getString(R.string.detail_recently_added), context.resources.getQuantityString(R.plurals.xx_new_songs, listSize, listSize)),
            DisplayableItem(R.layout.item_detail_recently_added_list, MediaId.headerId("recent horiz list"), ""),
            DisplayableItem(R.layout.item_detail_see_all, RECENTLY_ADDED_SEE_ALL, "")
    )

    fun albums(artistName: String?) : DisplayableItem {
        if (artistName != null){
            return DisplayableItem(R.layout.item_detail_header_albums, MediaId.headerId("detail albums"),
                    context.getString(R.string.detail_more_albums_by, artistName))
        }
        return DisplayableItem(R.layout.item_detail_header_albums, MediaId.headerId("detail albums"),
                context.resources.getStringArray(R.array.detail_album_header)[mediaId.source])
    }

    val shuffle = DisplayableItem(R.layout.item_detail_shuffle, MediaId.headerId("detail shuffle"), "")

    val songs = listOf(
            DisplayableItem(R.layout.item_detail_header_all_song, MediaId.headerId("detail songs header"), context.getString(R.string.detail_songs)),
            shuffle
    )

    val no_songs = DisplayableItem(R.layout.item_detail_empty_state, MediaId.headerId("detail empty state"), "")

}