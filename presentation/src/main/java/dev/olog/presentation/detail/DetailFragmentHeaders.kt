package dev.olog.presentation.detail

import android.content.Context
import androidx.core.os.bundleOf
import dev.olog.core.MediaId
import dev.olog.shared.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaId: MediaId
) {

    companion object {
        val RECENTLY_ADDED_SEE_ALL = MediaId.headerId("recently added header")
        val RELATED_ARTISTS_SEE_ALL = MediaId.headerId("related artist header")
    }

    val mostPlayed = listOf(
        DisplayableItem(
            R.layout.item_detail_header,
            MediaId.headerId("most played header"),
            context.getString(R.string.detail_most_played)
        ),
        DisplayableItem(
            R.layout.item_detail_list_most_played,
            MediaId.headerId("most played horiz list"),
            ""
        )
    )

    fun relatedArtists(showSeeAll: Boolean) = listOf(
        DisplayableItem(
            R.layout.item_detail_header,
            RELATED_ARTISTS_SEE_ALL,
            context.getString(R.string.detail_related_artists),
            extra = bundleOf("visible" to showSeeAll)
        ),
        DisplayableItem(
            R.layout.item_detail_list_related_artists,
            MediaId.headerId("related artist list"),
            ""
        )
    )

    fun recent(listSize: Int, showSeeAll: Boolean) = listOf(
        DisplayableItem(
            R.layout.item_detail_header_recently_added,
            RECENTLY_ADDED_SEE_ALL,
            context.getString(R.string.detail_recently_added),
            context.resources.getQuantityString(R.plurals.detail_xx_new_songs, listSize, listSize),
            extra = bundleOf("visible" to showSeeAll)
        ),
        DisplayableItem(
            R.layout.item_detail_list_recently_added,
            MediaId.headerId("recent horiz list"),
            ""
        )
    )

    fun albums() = listOf(
        albumHeader(),
        DisplayableItem(
            R.layout.item_detail_list_albums,
            MediaId.headerId("albums horiz list"),
            ""
        )
    )

    private fun albumHeader(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_detail_header_albums, MediaId.headerId("detail albums"),
            context.resources.getStringArray(R.array.detail_album_header)[mediaId.source]
        )
    }

    val shuffle = DisplayableItem(
        R.layout.item_detail_shuffle,
        MediaId.headerId("detail shuffle"),
        ""
    )

    val songs = listOf(
        DisplayableItem(
            R.layout.item_detail_header_all_song,
            MediaId.headerId("detail songs header"),
            context.getString(R.string.detail_tracks),
            context.getString(R.string.detail_sort_by).toLowerCase()
        ),
        shuffle
    )

    val no_songs = DisplayableItem(
        R.layout.item_detail_empty_state,
        MediaId.headerId("detail empty state"),
        ""
    )

}