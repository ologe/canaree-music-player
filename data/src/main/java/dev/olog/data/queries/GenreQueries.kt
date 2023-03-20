package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Genres.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.SortPreferences

internal class GenreQueries(
    private val contentResolver: ContentResolver,
    sortPrefs: SortPreferences
) : BaseQueries(sortPrefs, false) {

    fun getAll(): Cursor {

        val query = """
            SELECT $_ID, $NAME
            FROM $EXTERNAL_CONTENT_URI
            ORDER BY $DEFAULT_SORT_ORDER
        """

        return contentResolver.querySql(query)
    }

    fun countGenreSize(genreId: Id): Cursor {
        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getRelatedArtists(genreId: Id): Cursor {
        val query = """
             SELECT
                ${Members.ARTIST_ID},
                ${Members.ARTIST},
                ${Members.ALBUM_ARTIST},
                ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
            ORDER BY lower(${Members.ARTIST}) COLLATE UNICODE ASC
        """

        return contentResolver.querySql(query)
    }

    fun getRecentlyAdded(genreId: Id): Cursor {
        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}, ${Members.ARTIST_ID}, ${Members.ALBUM_ID},
                ${Members.TITLE}, ${Members.ARTIST}, ${Members.ALBUM}, ${Members.ALBUM_ARTIST},
                ${Members.DURATION}, ${Members.DATA}, ${Members.YEAR},
                ${Members.TRACK}, ${Members.DATE_ADDED}, ${Members.DATE_MODIFIED}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query)
    }

    fun getSongList(genreId: Id): Cursor {
        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}, ${Members.ARTIST_ID}, ${Members.ALBUM_ID},
                ${Members.TITLE}, ${Members.ARTIST}, ${Members.ALBUM}, ${Members.ALBUM_ARTIST},
                ${Members.DURATION}, ${Members.DATA}, ${Members.YEAR},
                ${Members.TRACK}, ${Members.DATE_ADDED}, ${Members.DATE_MODIFIED}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String {
        return "${isPodcast()}"
    }

}