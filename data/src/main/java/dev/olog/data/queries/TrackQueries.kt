package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
internal class TrackQueries(
    private val schedulers: Schedulers,
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferencesGateway,
    isPodcast: Boolean
) : BaseQueries(blacklistPrefs, sortPrefs, isPodcast) {

    suspend fun getAll(): Cursor = withContext(schedulers.io) {
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

        contentResolver.querySql(query, params)
    }

    suspend fun getByParam(id: Id): Cursor = withContext(schedulers.io) {
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

        contentResolver.querySql(query, arrayOf("$id") + params)
    }

    suspend fun getByAlbumId(albumId: Id): Cursor = withContext(schedulers.io) {
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
            WHERE $ALBUM_ID = ? AND ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        contentResolver.querySql(query, arrayOf("$albumId") + params)
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