package dev.olog.presentation.detail

import android.content.Context
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaIdModifier
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        val RELATED_ARTISTS_SEE_ALL = MediaId.headerId("related artist header")
    }

    fun biography(mediaId: MediaId): DisplayableItem? {
        if (mediaId.isArtist || mediaId.isAlbum){
            return DisplayableHeader(
                type = R.layout.item_detail_biography,
                mediaId = MediaId.headerId("biography"),
                title = ""
            )
        }
        return null
    }

    val mostPlayed: List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header,
            mediaId = MediaId.headerId("most played header"),
            title = context.getString(R.string.detail_most_played),
            visible = false
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_most_played,
            mediaId = MediaId.headerId("most played horiz list")
        )
    )

    fun relatedArtists(showSeeAll: Boolean): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header,
            mediaId = RELATED_ARTISTS_SEE_ALL,
            title = context.getString(R.string.detail_related_artists),
            visible = showSeeAll
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_related_artists,
            mediaId = MediaId.headerId("related artist list")
        )
    )

    fun recent(listSize: Int, showSeeAll: Boolean): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header_recently_added,
            mediaId = MediaId.headerId("recently added header"),
            title = context.getString(R.string.detail_recently_added),
            subtitle = context.resources.getQuantityString(
                R.plurals.detail_xx_new_songs,
                listSize,
                listSize
            ),
            visible = showSeeAll
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_recently_added,
            mediaId = MediaId.headerId("recent horiz list")
        )
    )

    fun albums(parentMediaId: MediaId): List<DisplayableItem> = listOf(
        albumHeader(parentMediaId),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_albums,
            mediaId = MediaId.headerId("albums horiz list")
        )
    )

    private fun albumHeader(parentMediaId: MediaId): DisplayableItem {
        return DisplayableHeader(
            type = R.layout.item_detail_header_albums,
            mediaId = MediaId.headerId("detail albums"),
            title = context.resources.getStringArray(R.array.detail_album_header)[parentMediaId.source]
        )
    }

    val shuffle: DisplayableItem = DisplayableHeader(
        type = R.layout.item_detail_shuffle,
        mediaId = MediaId.headerId("detail shuffle").copy(
            modifier = MediaIdModifier.SHUFFLE
        ),
        title = ""
    )

    val songs: List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header_all_song,
            mediaId = MediaId.headerId("detail songs header"),
            title = context.getString(R.string.detail_tracks),
            subtitle = context.getString(R.string.detail_sort_by).toLowerCase()
        ),
        shuffle
    )

    val no_songs: DisplayableItem = DisplayableHeader(
        type = R.layout.item_detail_empty_state,
        mediaId = MediaId.headerId("detail empty state"),
        title = ""
    )

}