package dev.olog.feature.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dev.olog.feature.base.model.DisplayableHeader
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

@ViewModelScoped
class DetailFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) {

    private val mediaId = MediaId.fromString(savedStateHandle.get<String>(DetailFragment.ARGUMENTS_MEDIA_ID)!!)

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
            title = context.getString(localization.R.string.detail_most_played),
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
            title = context.getString(localization.R.string.detail_related_artists),
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
            title = context.getString(localization.R.string.detail_recently_added),
            subtitle = context.resources.getQuantityString(
                localization.R.plurals.detail_xx_new_songs,
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

    fun albums(): List<DisplayableItem> = listOf(
        albumHeader(),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_albums,
            mediaId = MediaId.headerId("albums horiz list")
        )
    )

    private fun albumHeader(): DisplayableItem {
        return DisplayableHeader(
            type = R.layout.item_detail_header_albums,
            mediaId = MediaId.headerId("detail albums"),
            title = context.resources.getStringArray(localization.R.array.detail_album_header)[mediaId.source]
        )
    }

    val shuffle: DisplayableItem = DisplayableHeader(
        type = R.layout.item_detail_shuffle,
        mediaId = MediaId.headerId("detail shuffle"),
        title = ""
    )

    val songs: List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header_all_song,
            mediaId = MediaId.headerId("detail songs header"),
            title = context.getString(localization.R.string.detail_tracks),
            subtitle = context.getString(localization.R.string.detail_sort_by).toLowerCase()
        ),
        shuffle
    )

    val no_songs: DisplayableItem = DisplayableHeader(
        type = R.layout.item_detail_empty_state,
        mediaId = MediaId.headerId("detail empty state"),
        title = ""
    )

}