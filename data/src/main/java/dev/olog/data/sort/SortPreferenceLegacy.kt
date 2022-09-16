package dev.olog.data.sort

import android.content.SharedPreferences
import dev.olog.core.Migration
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.sort.SortType
import dev.olog.shared.extension.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@Migration
internal class SortPreferenceLegacy @Inject constructor(
    private val preferences: SharedPreferences,
) {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val ALL_SONGS_SORT_ORDER = "$TAG.ALL_SONG_SORT_ORDER"
        private const val ALL_ALBUMS_SORT_ORDER = "$TAG.ALL_ALBUMS_SORT_ORDER"
        private const val ALL_ARTISTS_SORT_ORDER = "$TAG.ALL_ARTISTS_SORT_ORDER"

        private const val ALL_ALBUMS_SORT_ARRANGING = "$TAG.ALL_ALBUMS_SORT_ARRANGING"
        private const val ALL_SONGS_SORT_ARRANGING = "$TAG.ALL_SONGS_SORT_ARRANGING"
        private const val ALL_ARTISTS_SORT_ARRANGING = "$TAG.ALL_ARTISTS_SORT_ARRANGING"

        private const val DETAIL_SORT_FOLDER_ORDER = "$TAG.DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = "$TAG.DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = "$TAG.DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = "$TAG.DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = "$TAG.DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_FOLDER_ARRANGING = "$TAG.DETAIL_SORT_FOLDER_ARRANGING"
        private const val DETAIL_SORT_PLAYLIST_ARRANGING = "$TAG.DETAIL_SORT_PLAYLIST_ARRANGING"
        private const val DETAIL_SORT_ALBUM_ARRANGING = "$TAG.DETAIL_SORT_ALBUM_ARRANGING"
        private const val DETAIL_SORT_ARTIST_ARRANGING = "$TAG.DETAIL_SORT_ARTIST_ARRANGING"
        private const val DETAIL_SORT_GENRE_ARRANGING = "$TAG.DETAIL_SORT_GENRE_ARRANGING"
    }

    fun getAllTracksSort(): Sort {
        val sort = preferences.getInt(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging =
            preferences.getInt(ALL_SONGS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    fun getAllAlbumsSort(): Sort {
        val sort = preferences.getInt(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging =
            preferences.getInt(ALL_ALBUMS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    fun getAllArtistsSort(): Sort {
        val sort = preferences.getInt(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal)
        val arranging =
            preferences.getInt(ALL_ARTISTS_SORT_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[sort],
            SortDirection.values()[arranging]
        )
    }

    fun observeDetailFolderSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_FOLDER_ARRANGING,
                    SortDirection.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    SortType.values()[type],
                    SortDirection.values()[arranging]
                )
            }
    }

    fun observeDetailPlaylistSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_PLAYLIST_ARRANGING,
                    SortDirection.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    SortType.values()[type],
                    SortDirection.values()[arranging]
                )
            }
    }

    fun observeDetailAlbumSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ALBUM_ARRANGING,
                    SortDirection.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    SortType.values()[type],
                    SortDirection.values()[arranging]
                )
            }
    }

    fun observeDetailArtistSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ARTIST_ARRANGING,
                    SortDirection.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    SortType.values()[type],
                    SortDirection.values()[arranging]
                )
            }
    }

    fun observeDetailGenreSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_GENRE_ARRANGING,
                    SortDirection.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    SortType.values()[type],
                    SortDirection.values()[arranging]
                )
            }
    }

    fun getDetailFolderSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_FOLDER_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[order],
            SortDirection.values()[arranging]
        )
    }

    fun getDetailPlaylistSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_PLAYLIST_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[order],
            SortDirection.values()[arranging]
        )
    }

    fun getDetailAlbumSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ALBUM_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[order],
            SortDirection.values()[arranging]
        )
    }

    fun getDetailArtistSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ARTIST_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[order],
            SortDirection.values()[arranging]
        )
    }

    fun getDetailGenreSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_GENRE_ARRANGING, SortDirection.ASCENDING.ordinal)
        return Sort(
            SortType.values()[order],
            SortDirection.values()[arranging]
        )
    }

}