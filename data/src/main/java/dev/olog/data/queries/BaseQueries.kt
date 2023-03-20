package dev.olog.data.queries

import android.provider.MediaStore.Audio.Media.IS_PODCAST
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences

abstract class BaseQueries(
    protected val sortPrefs: SortPreferences,
    protected val isPodcast: Boolean
) {

    protected fun isPodcast(): String {
        return if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
    }

    protected fun isRecentlyAdded(): String {
        // TODO time seems not to be supported on android 11, throws illegal token strftime
        // TODO also tried with julianday, same crash
//        return "strftime('%s','now') - $DATE_ADDED <= $RECENTLY_ADDED_TIME"
        return ""
    }

    protected fun songListSortOrder(category: MediaIdCategory, default: String): String {
        return QueryUtils.songListSortOrder(getSortType(category), default)
    }

    private fun getSortType(category: MediaIdCategory): SortEntity {
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