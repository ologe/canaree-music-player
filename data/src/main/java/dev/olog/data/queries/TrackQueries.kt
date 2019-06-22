package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences

internal class TrackQueries(
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferences,
    isPodcast: Boolean
) : BaseQueries(blacklistPrefs, sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $ARTIST,
                $ALBUM,
                ${Columns.ALBUM_ARTIST},
                $DURATION,
                $DATA, $YEAR,
                $TRACK,
                $DATE_ADDED,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    fun getByParam(id: Long): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $ARTIST,
                $ALBUM,
                ${Columns.ALBUM_ARTIST},
                $DURATION,
                $DATA, $YEAR,
                $TRACK,
                $DATE_ADDED,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $_ID = ? AND ${defaultSelection()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query, arrayOf("$id"))
    }

    private fun defaultSelection(): String {
        return isPodcast()
//        if (includeAll) {
//            return notBlacklisted()
//        }
//        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        return MediaStore.Audio.AudioColumns.TITLE_KEY
//        if (isPodcast) {
//            return "lower($TITLE) COLLATE UNICODE"
//        }
//
//        val (type, arranging) = sortGateway.getAllTracksSortOrder()
//        var sort = when (type) {
//            SortType.TITLE -> "lower($TITLE)"
//            SortType.ARTIST -> "lower(${Columns.ARTIST})"
//            SortType.ALBUM -> "lower(${Columns.ALBUM})"
//            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
//            SortType.DURATION -> DURATION
//            SortType.RECENTLY_ADDED -> DATE_ADDED
//            else -> "lower($TITLE)"
//        }
//
//        sort += " COLLATE UNICODE "
//
//        if (arranging == SortArranging.ASCENDING && type == SortType.RECENTLY_ADDED) {
//            // recently added works in reverse
//            sort += " DESC"
//        }
//        if (arranging == SortArranging.DESCENDING) {
//            if (type == SortType.RECENTLY_ADDED) {
//                // recently added works in reverse
//                sort += " ASC"
//            } else {
//                sort += " DESC"
//            }
//
//        }
//        return sort
    }

}