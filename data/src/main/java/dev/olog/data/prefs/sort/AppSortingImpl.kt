package dev.olog.data.prefs.sort

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortDetail
import dev.olog.core.prefs.SortPreferences
import javax.inject.Inject

internal class AppSortingImpl @Inject constructor(
    private val preferences: SharedPreferences,
    private val detailSortingHelper: DetailSortingHelper

) : SortPreferences, SortDetail by detailSortingHelper {

    companion object {
        const val TAG = "AppPreferencesDataStoreImpl"

        private const val ALL_SONGS_SORT_ORDER = "$TAG.ALL_SONG_SORT_ORDER"
        private const val ALL_ALBUMS_SORT_ORDER = "$TAG.ALL_ALBUMS_SORT_ORDER"
        private const val ALL_ARTISTS_SORT_ORDER = "$TAG.ALL_ARTISTS_SORT_ORDER"

        private const val ALL_ALBUMS_SORT_ARRANGING = "$TAG.ALL_ALBUMS_SORT_ARRANGING"
        private const val ALL_SONGS_SORT_ARRANGING = "$TAG.ALL_SONGS_SORT_ARRANGING"
        private const val ALL_ARTISTS_SORT_ARRANGING = "$TAG.ALL_ARTISTS_SORT_ARRANGING"
    }

    override fun getAllTracksSort(): Sort {
        val sort = preferences.getInt(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging =
            preferences.getInt(ALL_SONGS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    override fun getAllAlbumsSort(): Sort {
        val sort = preferences.getInt(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging =
            preferences.getInt(ALL_ALBUMS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    override fun getAllArtistsSort(): Sort {
        val sort = preferences.getInt(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal)
        val arranging =
            preferences.getInt(ALL_ARTISTS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    override fun setAllTracksSort(sortType: Sort) {
        preferences.edit {
            putInt(ALL_SONGS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_SONGS_SORT_ARRANGING, sortType.direction.ordinal)
        }
    }

    override fun setAllAlbumsSort(sortType: Sort) {
        preferences.edit {
            putInt(ALL_ALBUMS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ALBUMS_SORT_ARRANGING, sortType.direction.ordinal)
        }
    }

    override fun setAllArtistsSort(sortType: Sort) {
        preferences.edit {
            putInt(ALL_ARTISTS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ARTISTS_SORT_ARRANGING, sortType.direction.ordinal)
        }
    }


}