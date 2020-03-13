package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences

@Suppress("DEPRECATION")
internal class TrackQueries(
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferences,
    isPodcast: Boolean,
    val tableUri: Uri
) : BaseQueries(blacklistPrefs, sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $ARTIST,
                $ALBUM,
                ${Columns.ALBUM_ARTIST},
                $DURATION,
                $DATA,
                $TRACK,
                $DATE_ADDED,
                $DATE_MODIFIED,
                $IS_PODCAST,
                $DISPLAY_NAME
            FROM $tableUri
            WHERE ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query, params)
    }

    fun getByParam(id: Long): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $ARTIST,
                $ALBUM,
                ${Columns.ALBUM_ARTIST},
                $DURATION,
                $DATA, 
                $TRACK,
                $DATE_ADDED,
                $DATE_MODIFIED,
                $IS_PODCAST,
                $DISPLAY_NAME
            FROM $tableUri
            WHERE $_ID = ? AND $blacklist
        """

        return contentResolver.querySql(query, arrayOf("$id") + params)
    }

    fun getByAlbumId(albumId: Long): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $ARTIST,
                $ALBUM,
                ${Columns.ALBUM_ARTIST},
                $DURATION,
                $DATA, 
                $TRACK,
                $DATE_ADDED,
                $DATE_MODIFIED,
                $IS_PODCAST,
                $DISPLAY_NAME
            FROM $tableUri
            WHERE $ALBUM_ID = ? AND $blacklist
        """

        return contentResolver.querySql(query, arrayOf("$albumId") + params)
    }

    private fun defaultSelection(notBlacklisted: String): String {
        return "${isPodcast()} AND $notBlacklisted"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($TITLE) COLLATE UNICODE"
        }

        val sortEntity = sortPrefs.getAllTracksSort()
        var sort = when (sortEntity.type) {
            SortType.TITLE -> "lower($TITLE)"
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM -> "lower($ALBUM)"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            SortType.DURATION -> DURATION
            SortType.RECENTLY_ADDED -> DATE_ADDED
            else -> throw RuntimeException("sort not handled=${sortEntity.type}")
        }

        sort += " COLLATE UNICODE "

        if (sortEntity.type == SortType.RECENTLY_ADDED) {
            // recently added order works in reverse
            if (sortEntity.arranging == SortArranging.ASCENDING) {
                sort += " DESC"
            } else {
                sort += " ASC"
            }
        } else {
            if (sortEntity.arranging == SortArranging.ASCENDING) {
                sort += " ASC"
            } else {
                sort += " DESC"

            }
        }
        return sort
    }

}