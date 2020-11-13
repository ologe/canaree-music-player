package dev.olog.data.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.base.Path
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences

@Suppress("DEPRECATION")
internal class FolderQueries(
    private val contentResolver: ContentResolver,
    blacklistPrefs: BlacklistPreferences,
    sortPrefs: SortPreferences
) : BaseQueries(blacklistPrefs, sortPrefs, false) {

    fun getAll(includeBlackListed: Boolean): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
            SELECT $DATA
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(includeBlackListed, blacklist)}
        """
        if (includeBlackListed){
            return contentResolver.querySql(query)
        }
        return contentResolver.querySql(query, params)
    }

    fun getSongList(folderPath: String): Cursor {
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
        return contentResolver.querySql(query, params + arrayOf(folderPath))
    }

    fun getRecentlyAdded(folderPath: String): Cursor {
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
        return contentResolver.querySql(query, params + arrayOf(folderPath))
    }

    fun getRelatedArtists(path: Path): Cursor {
        val (blacklist, params) = notBlacklisted()

        val query = """
             SELECT
                $ARTIST_ID,
                $ARTIST,
                ${Columns.ALBUM_ARTIST},
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection(false, blacklist)} AND $folderProjection = ?
            ORDER BY lower($ARTIST) COLLATE UNICODE ASC
        """

        return contentResolver.querySql(query, params + arrayOf(path))
    }

    private fun defaultSelection(includeBlackListed: Boolean, notBlacklisted: String): String {
        if (includeBlackListed) {
            return isPodcast()
        }
        return "${isPodcast()} AND $notBlacklisted"
    }

}