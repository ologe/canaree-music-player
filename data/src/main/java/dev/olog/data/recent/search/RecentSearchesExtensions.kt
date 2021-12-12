package dev.olog.data.recent.search

import dev.olog.core.MediaId
import dev.olog.data.RecentSearchesQueries

internal fun RecentSearchesQueries.insert(
    mediaId: MediaId,
    insertion_time: Long,
) {
    insert(
        item_id = when {
            mediaId.isLeaf -> mediaId.leaf!!.toString()
            else -> mediaId.categoryValue
        },
        type = mediaId.category.recentSearchType(),
        insertion_time = insertion_time,
        media_id = mediaId
    )
}