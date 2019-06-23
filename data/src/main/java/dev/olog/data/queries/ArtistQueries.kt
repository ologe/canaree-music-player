package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences

internal class ArtistQueries(
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferences,
    isPodcast: Boolean
) : BaseQueries(blacklistPrefs, sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${Columns.ALBUM_ARTIST},
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}

            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    fun getById(id: Id): Cursor {
        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${Columns.ALBUM_ARTIST},
                $DATA,
                $IS_PODCAST
            FROM ${EXTERNAL_CONTENT_URI}
            WHERE $ARTIST_ID = ? AND ${defaultSelection()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query, arrayOf("$id"))
    }

    fun getSongList(id: Long): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${Columns.ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSongSelection()} AND $ARTIST_ID = ?
            ORDER BY ${songListSortOrder(MediaIdCategory.ARTISTS, DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query, arrayOf("$id"))
    }

    fun getRecentlyAdded(): Cursor {
        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${Columns.ALBUM_ARTIST},
                $DATA,
                $IS_PODCAST
            FROM ${EXTERNAL_CONTENT_URI}
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun defaultSongSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($ARTIST) COLLATE UNICODE"
        }

        val (type, arranging) = sortPrefs.getAllArtistsSortOrder()
        var sort = when (type) {
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            else -> "lower($ARTIST)"
        }
        sort += " COLLATE UNICODE "
        if (arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return sort
    }

}