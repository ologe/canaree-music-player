package dev.olog.presentation.fragment_detail

import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class DetailHeaders @Inject constructor(
        @ApplicationContext private val context: Context,
        mediaId: String
) {

    companion object {
        const val RECENTLY_ADDED_ID = "recent id with see all"
        const val ALBUMS_ID = "albums id with see all"
    }

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    val mostPlayed = listOf(
            DisplayableItem(R.layout.item_detail_header, "most played id", context.getString(R.string.detail_most_played)),
            DisplayableItem(R.layout.item_detail_most_played_horizontal_list, "most played list", "")
    )

    val recent = listOf(
            DisplayableItem(R.layout.item_detail_header, "recent id", context.getString(R.string.detail_recently_added)),
            DisplayableItem(R.layout.item_detail_recent_horizontal_list, "recent list", "")
    )

    val recentWithSeeAll = listOf(
            DisplayableItem(R.layout.item_detail_header, RECENTLY_ADDED_ID, context.getString(R.string.detail_recently_added), context.getString(R.string.detail_see_more)),
            DisplayableItem(R.layout.item_detail_recent_horizontal_list, "recent list", "")
    )

    val albums : DisplayableItem = DisplayableItem(R.layout.item_detail_header, "albums id",
            context.resources.getStringArray(R.array.detail_album_header)[source])

    val albumsWithSeeAll : DisplayableItem = DisplayableItem(R.layout.item_detail_header, ALBUMS_ID,
            context.resources.getStringArray(R.array.detail_album_header)[source],
            context.getString(R.string.detail_see_more))

    val songs = listOf(
            DisplayableItem(R.layout.item_detail_header, "songs id", context.getString(R.string.detail_songs)),
            DisplayableItem(R.layout.item_detail_shuffle, "shuffle id", "")
    )

}