package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.Id
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

    fun getByParam(id: Id): Cursor {
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
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($TITLE) COLLATE UNICODE"
        }

        val (type, arranging) = sortPrefs.getAllTracksSortOrder()
        var sort = when (type) {
            SortType.TITLE -> "lower($TITLE)"
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM -> "lower($ALBUM)"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            SortType.DURATION -> DURATION
            SortType.RECENTLY_ADDED -> DATE_ADDED
            else -> "lower($TITLE)"
        }

        sort += " COLLATE UNICODE "

        if (type == SortType.RECENTLY_ADDED) {
            // recently added order works in reverse
            if (arranging == SortArranging.ASCENDING) {
                sort += " DESC"
            } else {
                sort += " ASC"
            }
        } else {
            if (arranging == SortArranging.ASCENDING) {
                sort += " ASC"
            } else {
                sort += " DESC"

            }
        }
        return sort
    }

}