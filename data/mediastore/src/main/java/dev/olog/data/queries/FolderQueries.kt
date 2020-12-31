package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.base.Path
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
internal class FolderQueries(
    private val schedulers: Schedulers,
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferencesGateway
) : BaseQueries(blacklistPrefs, sortPrefs, false) {

    suspend fun getAll(includeBlackListed: Boolean): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $DATA
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(includeBlackListed, blacklist)}
        """
        if (includeBlackListed){
            contentResolver.querySql(query)
        } else {
            contentResolver.querySql(query, params)
        }
    }

    suspend fun getSongList(folderPath: Path): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${Columns.ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $DATE_MODIFIED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(false, blacklist)} AND $folderProjection = ?
            ORDER BY ${songListSortOrder(MediaIdCategory.FOLDERS, DEFAULT_SORT_ORDER)}
        """
        contentResolver.querySql(query, params + arrayOf(folderPath))
    }

    suspend fun getRecentlyAdded(folderPath: Path): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE, $ARTIST, $ALBUM, ${Columns.ALBUM_ARTIST},
                $DURATION, $DATA, $YEAR,
                $TRACK, $DATE_ADDED, $DATE_MODIFIED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(false, blacklist)} AND $folderProjection = ? ${isRecentlyAdded()}
            ORDER BY lower($TITLE) COLLATE UNICODE ASC
        """
        contentResolver.querySql(query, params + arrayOf(folderPath))
    }

    suspend fun getRelatedArtists(path: Path): Cursor = withContext(schedulers.io) {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $ARTIST_ID, $ARTIST, ${Columns.ALBUM_ARTIST}, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(false, blacklist)} AND $folderProjection = ?
            ORDER BY lower($ARTIST) COLLATE UNICODE ASC
        """

        contentResolver.querySql(query, params + arrayOf(path))
    }

    private fun defaultSelection(includeBlackListed: Boolean, notBlacklisted: String): String {
        if (includeBlackListed) {
            return isPodcast()
        }
        return "${isPodcast()} AND $notBlacklisted"
    }

}