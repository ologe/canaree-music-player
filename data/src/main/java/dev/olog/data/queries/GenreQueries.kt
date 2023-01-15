package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Genres.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.SortPreferences

@Suppress("DEPRECATION")
internal class GenreQueries(
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistGateway,
    sortPrefs: SortPreferences
) : BaseQueries(blacklistPrefs, sortPrefs, false) {

    fun getAll(): Cursor {

        val query = """
            SELECT $_ID, $NAME
            FROM $EXTERNAL_CONTENT_URI
            ORDER BY $DEFAULT_SORT_ORDER
        """

        return contentResolver.querySql(query)
    }

    fun countGenreSize(genreId: Id): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)}
        """
        return contentResolver.querySql(query, params)
    }

    fun getRelatedArtists(genreId: Id): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
             SELECT
                ${Members.ARTIST_ID},
                ${Members.ARTIST},
                ${Columns.ALBUM_ARTIST},
                ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)}
            ORDER BY lower(${Members.ARTIST}) COLLATE UNICODE ASC
        """

        return contentResolver.querySql(query, params)
    }

    fun getRecentlyAdded(genreId: Id): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}, ${Members.ARTIST_ID}, ${Members.ALBUM_ID},
                ${Members.TITLE}, ${Members.ARTIST}, ${Members.ALBUM}, ${Columns.ALBUM_ARTIST},
                ${Members.DURATION}, ${Members.DATA}, ${Members.YEAR},
                ${Members.TRACK}, ${Members.DATE_ADDED}, ${Members.DATE_MODIFIED}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)} AND ${isRecentlyAdded()}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query, params)
    }

    fun getSongList(genreId: Id): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}, ${Members.ARTIST_ID}, ${Members.ALBUM_ID},
                ${Members.TITLE}, ${Members.ARTIST}, ${Members.ALBUM}, ${Columns.ALBUM_ARTIST},
                ${Members.DURATION}, ${Members.DATA}, ${Members.YEAR},
                ${Members.TRACK}, ${Members.DATE_ADDED}, ${Members.DATE_MODIFIED}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
        """
        return contentResolver.querySql(query, params)
    }

    private fun defaultSelection(notBlacklisted: String): String {
        return "${isPodcast()} AND $notBlacklisted"
    }

}