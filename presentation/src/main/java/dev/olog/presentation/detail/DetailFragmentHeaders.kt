package dev.olog.presentation.detail

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailItem
import javax.inject.Inject

class DetailFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object { // TODO move to another place
        private const val RELATED_ARTISTS_LIMIT = 10
        const val NESTED_SPAN_COUNT = 4
        const val RECENTLY_ADDED_LIMIT = NESTED_SPAN_COUNT * 4
    }

    fun mostPlayed(items: List<DetailItem.MostPlayed>): List<DetailItem> {
        if (items.isEmpty()) return emptyList()
        return listOf(
            DetailItem.Header(context.getString(R.string.detail_most_played)),
            DetailItem.MostPlayedList(items)
        )
    }

    fun relatedArtists(
        mediaId: MediaId,
        items: List<DetailItem.Album>
    ): List<DetailItem> {
        if (items.isEmpty()) return emptyList()
        return listOf(
            DetailItem.HeaderRelatedArtists(
                mediaId = mediaId,
                showSeeAll = items.size > RELATED_ARTISTS_LIMIT,
            ),
            DetailItem.RelatedArtistsList(items)
        )
    }

    fun recentlyAdded(
        mediaId: MediaId,
        items: List<DetailItem.RecentlyAdded>
    ): List<DetailItem> {
        if (items.isEmpty()) return emptyList()
        return listOf(
            DetailItem.HeaderRecentlyAdded(
                mediaId = mediaId,
                itemsCount = items.size,
                showSeeAll = items.size > RECENTLY_ADDED_LIMIT,
            ),
            DetailItem.RecentlyAddedList(items)
        )
    }

    fun albums(
        mediaId: MediaId,
        items: List<DetailItem.Album>,
    ): List<DetailItem> {
        if (items.isEmpty()) return emptyList()
        return listOf(
            DetailItem.HeaderSiblings(context.resources.getStringArray(R.array.detail_album_header)[mediaId.source]),
            DetailItem.SiblingsList(items)
        )
    }

    fun songs(mediaId: MediaId, sort: SortEntity): List<DetailItem> = listOf(
        DetailItem.HeaderSongs(mediaId, sort),
        DetailItem.Shuffle(mediaId)
    )

}