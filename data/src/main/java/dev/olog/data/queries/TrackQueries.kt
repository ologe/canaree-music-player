package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.SortPreferences
import dev.olog.feature.library.LibraryPrefs

@Suppress("DEPRECATION")
internal class TrackQueries(
    private val contentResolver: ContentResolver,
    libraryPrefs: LibraryPrefs,
    sortPrefs: SortPreferences,
    isPodcast: Boolean
) : BaseQueries(libraryPrefs, sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val (blacklist, params) = notBlacklisted()

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
                $DATE_MODIFIED,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query, params)
    }

    fun getByParam(id: Id): Cursor {
        val (blacklist, params) = notBlacklisted()

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
                $DATE_MODIFIED,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $_ID = ? AND ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query, arrayOf("$id") + params)
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
            else -> "lower($TITLE)"
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