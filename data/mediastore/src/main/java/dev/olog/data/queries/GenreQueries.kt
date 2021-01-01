package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Genres.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.mediaid.MediaIdCategory
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
internal class GenreQueries(
    private val schedulers: Schedulers,
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferencesGateway
) : BaseQueries(blacklistPrefs, sortPrefs, false) {

    suspend fun getAll(): Cursor = withContext(schedulers.io) {

        val query = """
            SELECT $_ID, $NAME
            FROM $EXTERNAL_CONTENT_URI
            ORDER BY $DEFAULT_SORT_ORDER
        """

        contentResolver.querySql(query)
    }

    suspend fun getByParam(id: Id): Cursor = withContext(schedulers.io) {

        val query = """
            SELECT $_ID, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE $_ID = ?
            ORDER BY $DEFAULT_SORT_ORDER
        """

        contentResolver.querySql(query, arrayOf("$id"))
    }

    suspend fun countGenreSize(genreId: Id): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)}
        """
        contentResolver.querySql(query, params)
    }

    suspend fun getRelatedArtists(genreId: Id): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
             SELECT ${Members.ARTIST_ID}, ${Members.ARTIST}, ${Columns.ALBUM_ARTIST}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)}
            ORDER BY lower(${Members.ARTIST}) COLLATE UNICODE ASC
        """

        contentResolver.querySql(query, params)
    }

    suspend fun getRecentlyAdded(genreId: Id): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT ${Members._ID}, ${Members.AUDIO_ID}, ${Members.ARTIST_ID}, ${Members.ALBUM_ID},
                ${Members.TITLE}, ${Members.ARTIST}, ${Members.ALBUM}, ${Columns.ALBUM_ARTIST},
                ${Members.DURATION}, ${Members.DATA}, ${Members.YEAR},
                ${Members.TRACK}, ${Members.DATE_ADDED}, ${Members.DATE_MODIFIED}, ${Members.IS_PODCAST}
            FROM ${Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection(blacklist)} ${isRecentlyAdded()}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
        """
        contentResolver.querySql(query, params)
    }

    suspend fun getSongList(genreId: Id): Cursor = withContext(schedulers.io) {
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
        contentResolver.querySql(query, params)
    }

    private fun defaultSelection(notBlacklisted: String): String {
        return "${isPodcast()} AND $notBlacklisted"
    }

}