package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences

internal class ArtistQueries(
    private val contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    isPodcast: Boolean
) : BaseQueries(sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${ALBUM_ARTIST},
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    fun getSongList(id: Long): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $DATE_MODIFIED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ARTIST_ID = ? AND ${defaultSongSelection()}
            ORDER BY ${songListSortOrder(MediaIdCategory.ARTISTS, DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query, arrayOf("$id"))
    }

    fun getRecentlyAdded(): Cursor {
        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${ALBUM_ARTIST},
                $DATA,
                $IS_PODCAST
            FROM ${EXTERNAL_CONTENT_URI}
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String {
        return "${isPodcast()}"
    }

    private fun defaultSongSelection(): String {
        return "${isPodcast()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($ARTIST) COLLATE UNICODE"
        }

        val sortEntity = sortPrefs.getAllArtistsSort()
        var sort = when (sortEntity.type) {
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM_ARTIST -> "lower(${ALBUM_ARTIST})"
            else -> "lower($ARTIST)"
        }
        sort += " COLLATE UNICODE "
        if (sortEntity.arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return sort
    }

}