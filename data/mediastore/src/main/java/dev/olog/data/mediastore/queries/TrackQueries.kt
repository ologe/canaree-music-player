package dev.olog.data.mediastore.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.domain.entity.Sort
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
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
            Sort.Type.TITLE -> "lower($TITLE)"
            Sort.Type.ARTIST -> "lower($ARTIST)"
            Sort.Type.ALBUM -> "lower($ALBUM)"
            Sort.Type.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            Sort.Type.DURATION -> DURATION
            Sort.Type.RECENTLY_ADDED -> DATE_ADDED
            else -> "lower($TITLE)"
        }

        sort += " COLLATE UNICODE "

        if (sortEntity.type == Sort.Type.RECENTLY_ADDED) {
            // recently added order works in reverse
            if (sortEntity.arranging == Sort.Arranging.ASCENDING) {
                sort += " DESC"
            } else {
                sort += " ASC"
            }
        } else {
            if (sortEntity.arranging == Sort.Arranging.ASCENDING) {
                sort += " ASC"
            } else {
                sort += " DESC"

            }
        }
        return sort
    }

}