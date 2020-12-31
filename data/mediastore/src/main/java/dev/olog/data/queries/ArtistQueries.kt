package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.base.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
internal class ArtistQueries(
    private val schedulers: Schedulers,
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferencesGateway,
    isPodcast: Boolean
) : BaseQueries(blacklistPrefs, sortPrefs, isPodcast) {

    suspend fun getAll(): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $ARTIST_ID, $ARTIST, ${Columns.ALBUM_ARTIST}, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        contentResolver.querySql(query, params)
    }

    suspend fun getByParam(id: Id): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
             SELECT $ARTIST_ID, $ARTIST, ${Columns.ALBUM_ARTIST}, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ARTIST_ID = ? AND ${defaultSelection(blacklist)}
            ORDER BY ${sortOrder()}
        """

        contentResolver.querySql(query, arrayOf("$id") + params)
    }

    suspend fun getSongList(id: Id): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${Columns.ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $DATE_MODIFIED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ARTIST_ID = ? AND ${defaultSongSelection(blacklist)}
            ORDER BY ${songListSortOrder(MediaIdCategory.ARTISTS, DEFAULT_SORT_ORDER)}
        """
        contentResolver.querySql(query, arrayOf("$id") + params)
    }

    suspend fun getRecentlyAdded(): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $ARTIST_ID, $ARTIST, ${Columns.ALBUM_ARTIST}, $DATA, $IS_PODCAST
            FROM ${EXTERNAL_CONTENT_URI}
            WHERE ${defaultSelection(blacklist)} ${isRecentlyAdded()}
            ORDER BY ${sortOrder()}
        """

        contentResolver.querySql(query, params)
    }

    private fun defaultSelection(notBlacklisted: String): String {
        return "${isPodcast()} AND $notBlacklisted"
    }

    private fun defaultSongSelection(notBlacklisted: String): String {
        return "${isPodcast()} AND $notBlacklisted"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($ARTIST) COLLATE UNICODE"
        }

        val sortEntity = sortPrefs.getAllArtistsSort()
        var sort = when (sortEntity.type) {
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            else -> "lower($ARTIST)"
        }
        sort += " COLLATE UNICODE "
        if (sortEntity.arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return sort
    }

}