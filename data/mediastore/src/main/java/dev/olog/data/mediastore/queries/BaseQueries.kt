package dev.olog.data.mediastore.queries

import android.provider.MediaStore.Audio.Media.*
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.Sort
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import kotlin.time.days

@Suppress("DEPRECATION")
abstract class BaseQueries(
    protected val blacklistPrefs: BlacklistPreferences,
    protected val sortPrefs: SortPreferencesGateway,
    protected val isPodcast: Boolean
) {

    companion object {
        private val RECENTLY_ADDED_TIME = 14.days.inSeconds
    }

    protected val folderProjection: String
        get() = "substr($DATA, 1, length($DATA) - length($DISPLAY_NAME) - 1)"

    private  val discNumberProjection = "CASE WHEN $TRACK >= 1000 THEN substr($TRACK, 1, 1) ELSE 0 END"
    private  val trackNumberProjection = "CASE WHEN $TRACK >= 1000 THEN $TRACK % 1000 ELSE $TRACK END"

    protected fun isPodcast(): String {
        return if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
    }

    protected fun isRecentlyAdded(): String {
        // TODO time seems not to be supported on android 11, throws illegal token strftime
        // TODO also tried with julianday, same crash
//        return "AND strftime('%s','now') - $DATE_ADDED <= $RECENTLY_ADDED_TIME"
        return ""
    }

    protected fun notBlacklisted(): Pair<String, Array<String>> {
        val blacklist = blacklistPrefs.getBlackList()
        val params = blacklist.map { "?" }
        val blackListed = blacklist.toTypedArray()
        return "$folderProjection NOT IN (${params.joinToString()})" to blackListed
    }

    protected fun songListSortOrder(category: MediaIdCategory, default: String): String {

        val sortEntity = getSortType(category)
        var sort = when (sortEntity.type) {
            Sort.Type.TITLE -> "lower($TITLE)"
            Sort.Type.ARTIST -> "lower($ARTIST)"
            Sort.Type.ALBUM -> "lower($ALBUM)"
            Sort.Type.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            Sort.Type.RECENTLY_ADDED -> DATE_ADDED // DESC
            Sort.Type.DURATION -> DURATION
            Sort.Type.TRACK_NUMBER -> "$discNumberProjection ${sortEntity.arranging}, $trackNumberProjection ${sortEntity.arranging}, $TITLE"
            Sort.Type.CUSTOM -> default
            else -> "lower($TITLE)"
        }

        if (sortEntity.type == Sort.Type.CUSTOM) {
            return sort
        }

        sort += " COLLATE UNICODE "

        if (sortEntity.arranging == Sort.Arranging.ASCENDING && sortEntity.type == Sort.Type.RECENTLY_ADDED) {
            // recently added works in reverse
            sort += " DESC"
        }
        if (sortEntity.arranging == Sort.Arranging.DESCENDING) {
            if (sortEntity.type == Sort.Type.RECENTLY_ADDED) {
                // recently added works in reverse
                sort += " ASC"
            } else {
                sort += " DESC"
            }

        }
        return sort
    }

    private fun getSortType(category: MediaIdCategory): Sort {
        return when (category) {
            MediaIdCategory.FOLDERS -> sortPrefs.getDetailFolderSort()
            MediaIdCategory.PLAYLISTS -> sortPrefs.getDetailPlaylistSort()
            MediaIdCategory.ALBUMS -> sortPrefs.getDetailAlbumSort()
            MediaIdCategory.ARTISTS -> sortPrefs.getDetailArtistSort()
            MediaIdCategory.GENRES -> sortPrefs.getDetailFolderSort()
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

}