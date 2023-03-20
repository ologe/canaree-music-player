package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.SortPreferences

internal class AlbumsQueries(
    private val contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    isPodcast: Boolean
) : BaseQueries(sortPrefs, isPodcast) {

    fun getAll(): Cursor {
        val query = """
             SELECT
                $ALBUM_ID,
                $ARTIST_ID,
                $ARTIST,
                $ALBUM,
                ${ALBUM_ARTIST},
                $DATA,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            ORDER BY ${sortOrder()}
        """

        return contentResolver.querySql(query)
    }

    fun getSongList(id: Id): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $DATE_MODIFIED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID = ? AND ${defaultSelection()}
            ORDER BY ${songListSortOrder(MediaIdCategory.ALBUMS, DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query, arrayOf("$id"))
    }

    fun getRecentlyAdded(): Cursor {
        val query = """
            SELECT 
                $ALBUM_ID,
                $ARTIST_ID,
                $ARTIST,
                $ALBUM,
                ${ALBUM_ARTIST},
                $DATA,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}

            ORDER BY $DATE_ADDED DESC
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String {
        return "${isPodcast()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($ALBUM) COLLATE UNICODE"
        }

        val sortEntity = sortPrefs.getAllAlbumsSort()
        var sort = when (sortEntity.type) {
            SortType.ALBUM -> "lower($ALBUM)"
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM_ARTIST -> "lower(${ALBUM_ARTIST})"
            else -> "lower($ALBUM)"
        }

        sort += " COLLATE UNICODE "

        if (sortEntity.arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return sort
    }

}