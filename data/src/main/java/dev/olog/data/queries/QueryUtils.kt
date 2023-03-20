package dev.olog.data.queries

import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType

object QueryUtils {

    const val MOST_PLAYED_HAVE_AT_LEAST = 5
    const val MOST_PLAYED_LIMIT = 10
    const val LAST_PLAYED_MAX_ITEM_TO_SHOW = 10

    private const val DISC_NUMBER_PROJECTION = "CASE WHEN ${AudioColumns.TRACK} >= 1000 THEN substr(${AudioColumns.TRACK}, 1, 1) ELSE 0 END"
    private const val TRACK_NUMBER_PROJECTION = "CASE WHEN ${AudioColumns.TRACK} >= 1000 THEN ${AudioColumns.TRACK} % 1000 ELSE ${AudioColumns.TRACK} END"
    private const val RECENTLY_ADDED_TIME = 1209600 // 14.days.inWholeSeconds
    const val RECENTLY_ADDED = "strftime('%s','now') - ${AudioColumns.DATE_ADDED} <= $RECENTLY_ADDED_TIME"

    fun songListSortOrder(
        sort: SortEntity,
        custom: String,
    ): String {
        val direction = sort.arranging
        return when (sort.type) {
            SortType.TITLE -> "${AudioColumns.TITLE} $direction"
            SortType.ARTIST -> "CASE WHEN ${AudioColumns.ARTIST} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ARTIST} $direction, ${AudioColumns.TITLE} $direction"
            SortType.ALBUM -> "CASE WHEN ${AudioColumns.ALBUM} = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, ${AudioColumns.ALBUM} $direction, ${AudioColumns.TITLE} $direction"
            SortType.ALBUM_ARTIST -> "${AudioColumns.ALBUM_ARTIST} $direction, ${AudioColumns.TITLE} $direction"
            SortType.DURATION -> "${AudioColumns.DURATION} $direction"
            SortType.RECENTLY_ADDED -> "${AudioColumns.DATE_ADDED} ${!direction}, ${AudioColumns.TITLE} $direction"
            SortType.TRACK_NUMBER -> "$DISC_NUMBER_PROJECTION ${direction}, $TRACK_NUMBER_PROJECTION ${direction}, ${AudioColumns.TITLE} $direction"
            SortType.CUSTOM -> custom
        }
    }

}