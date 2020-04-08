package dev.olog.presentation.detail

import android.content.Context
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.presentation.base.model.PresentationIdCategory.ALBUMS
import dev.olog.feature.presentation.base.model.PresentationIdCategory.ARTISTS
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableNestedListPlaceholder
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
    private val context: Context,
    private val mediaId: PresentationId.Category
) {

    companion object {
        val RELATED_ARTISTS_SEE_ALL = headerId("related artist header")
    }

    fun biography(mediaId: PresentationIdCategory): DisplayableItem? {
        if (mediaId == ARTISTS || mediaId == ALBUMS){
            return DisplayableHeader(
                type = R.layout.item_detail_biography,
                mediaId = headerId("biography"),
                title = ""
            )
        }
        return null
    }

    val mostPlayed: List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header,
            mediaId = headerId("most played header"),
            title = context.getString(R.string.detail_most_played),
            visible = false
        ),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_most_played,
            mediaId = headerId("most played horiz list")
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
            mediaId = headerId("related artist list")
        )
    )

    fun recent(listSize: Int, showSeeAll: Boolean): List<DisplayableItem> = listOf(
        DisplayableHeader(
            type = R.layout.item_detail_header_recently_added,
            mediaId = headerId("recently added header"),
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
            mediaId = headerId("recent horiz list")
        )
    )

    fun albums(): List<DisplayableItem> = listOf(
        albumHeader(),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_albums,
            mediaId = headerId("albums horiz list")
        )
    )

    private fun albumHeader(): DisplayableItem {
        val index = mediaId.category.ordinal
        val title = context.resources.getStringArray(R.array.detail_album_header)[index]
        return DisplayableHeader(
            type = R.layout.item_detail_header_albums,
            mediaId = headerId("detail albums"),
            title = title
        )
    }

    fun spotifyAlbums(): List<DisplayableItem> = listOf(
        spotifyHeaderAlbums(),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_spotify_albums,
            mediaId = headerId("spotify albums horiz list")
        )
    )

    private fun spotifyHeaderAlbums(): DisplayableItem {
        return DisplayableHeader(
            type = R.layout.item_detail_header_albums,
            mediaId = headerId("detail spotify albums"),
            title = context.getString(R.string.category_albums)
        )
    }

    fun spotifySingles(): List<DisplayableItem> = listOf(
        spotifyHeaderSingles(),
        DisplayableNestedListPlaceholder(
            type = R.layout.item_detail_list_spotify_singles,
            mediaId = headerId("spotify singles horiz list")
        )
    )

    private fun spotifyHeaderSingles(): DisplayableItem {
        return DisplayableHeader(
            type = R.layout.item_detail_header_albums,
            mediaId = headerId("detail spotify singles"),
            title = "Singles" // TODO localization
        )
    }

    val shuffle: DisplayableItem =
        DisplayableHeader(
            type = R.layout.item_detail_shuffle,
            mediaId = headerId("detail shuffle"),
            title = ""
        )

    fun songs(isPodcast: Boolean): List<DisplayableItem> {
        val title = if (isPodcast) {
            R.string.detail_episodes
        } else {
            R.string.detail_tracks
        }
        val header = DisplayableHeader(
            type = R.layout.item_detail_header_all_song,
            mediaId = headerId("detail songs header"),
            title = context.getString(title),
            subtitle = context.getString(R.string.detail_sort_by).toLowerCase()
        )
        return if (isPodcast) {
            listOf(header)
        } else {
            listOf(header, shuffle)
        }
    }

    fun no_songs(isPodcast: Boolean): DisplayableItem {
        val layout = if (isPodcast) {
            R.layout.item_detail_empty_state_podcast
        } else {
            R.layout.item_detail_empty_state_songs
        }
        return DisplayableHeader(
            type = layout,
            mediaId = headerId("detail empty state"),
            title = ""
        )
    }

}