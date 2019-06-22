package dev.olog.data.queries

import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.SortArranging
import dev.olog.core.entity.SortType
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import java.util.concurrent.TimeUnit

abstract class BaseQueries(
    protected val blacklistPrefs: BlacklistPreferences,
    protected val sortPrefs: SortPreferences,
    protected val isPodcast: Boolean
) {

    companion object {
        private val RECENTLY_ADDED_TIME = TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)
    }

    protected val folderProjection: String
        get() = "substr($DATA, 1, length($DATA) - length($DISPLAY_NAME) - 1)"

    private  val discNumberProjection = "CASE WHEN $TRACK >= 1000 THEN substr($TRACK, 1, 1) ELSE 0 END"
    private  val trackNumberProjection = "CASE WHEN $TRACK >= 1000 THEN $TRACK % 1000 ELSE $TRACK END"

    protected fun isPodcast(): String {
        return if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
    }

    protected fun isRecentlyAdded(): String {
        return "strftime('%s','now') - $DATE_ADDED <= $RECENTLY_ADDED_TIME"
    }

    protected fun notBlacklisted(): String {
        val blackListed = blacklistPrefs.getBlackList().map { "'$it'" }
        return "$folderProjection NOT IN (${blackListed.joinToString()})"
    }

    protected fun songListSortOrder(category: MediaIdCategory, default: String): String {

        val type = getSortType(category)
        val arranging = sortPrefs.getDetailSortArranging()
        var sort = when (type) {
            SortType.TITLE -> "lower($TITLE)"
            SortType.ARTIST -> "lower($ARTIST)"
            SortType.ALBUM -> "lower($ALBUM)"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            SortType.RECENTLY_ADDED -> DATE_ADDED // DESC
            SortType.DURATION -> DURATION
            SortType.TRACK_NUMBER -> "$discNumberProjection $arranging, $trackNumberProjection $arranging, $TITLE"
            SortType.CUSTOM -> default
            else -> "lower($TITLE)"
        }

        if (type == SortType.CUSTOM) {
            return sort
        }

        sort += " COLLATE UNICODE "

        if (arranging == SortArranging.ASCENDING && type == SortType.RECENTLY_ADDED) {
            // recently added works in reverse
            sort += " DESC"
        }
        if (arranging == SortArranging.DESCENDING) {
            if (type == SortType.RECENTLY_ADDED) {
                // recently added works in reverse
                sort += " ASC"
            } else {
                sort += " DESC"
            }

        }
        return sort
    }

    private fun getSortType(category: MediaIdCategory): SortType {
        return when (category) {
            MediaIdCategory.FOLDERS -> sortPrefs.getDetailAlbumSortOrder()
            MediaIdCategory.PLAYLISTS -> sortPrefs.getDetailPlaylistSortOrder()
            MediaIdCategory.ALBUMS -> sortPrefs.getDetailAlbumSortOrder()
            MediaIdCategory.ARTISTS -> sortPrefs.getDetailArtistSortOrder()
            MediaIdCategory.GENRES -> sortPrefs.getDetailGenreSortOrder()
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

}