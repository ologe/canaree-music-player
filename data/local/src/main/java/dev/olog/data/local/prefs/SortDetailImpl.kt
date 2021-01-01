package dev.olog.data.local.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.Sort
import dev.olog.domain.prefs.SortDetail
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class SortDetailImpl @Inject constructor(
    private val preferences: SharedPreferences
) : SortDetail {

    companion object {
        private const val TAG = SortPreferencesGatewayImpl.TAG
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

    override fun observeDetailFolderSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_FOLDER_ORDER, Sort.Type.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_FOLDER_ARRANGING,
                    Sort.Arranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    Sort.Type.values()[type],
                    Sort.Arranging.values()[arranging]
                )
            }
    }

    override fun observeDetailPlaylistSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_PLAYLIST_ORDER, Sort.Type.CUSTOM.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_PLAYLIST_ARRANGING,
                    Sort.Arranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    Sort.Type.values()[type],
                    Sort.Arranging.values()[arranging]
                )
            }
    }

    override fun observeDetailAlbumSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_ALBUM_ORDER, Sort.Type.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ALBUM_ARRANGING,
                    Sort.Arranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    Sort.Type.values()[type],
                    Sort.Arranging.values()[arranging]
                )
            }
    }

    override fun observeDetailArtistSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_ARTIST_ORDER, Sort.Type.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ARTIST_ARRANGING,
                    Sort.Arranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    Sort.Type.values()[type],
                    Sort.Arranging.values()[arranging]
                )
            }
    }

    override fun observeDetailGenreSort(): Flow<Sort> {
        return preferences.observeKey(DETAIL_SORT_GENRE_ORDER, Sort.Type.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_GENRE_ARRANGING,
                    Sort.Arranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                Sort(
                    Sort.Type.values()[type],
                    Sort.Arranging.values()[arranging]
                )
            }
    }

    override fun setDetailFolderSort(sortType: Sort.Type) {
        return preferences.edit { putInt(DETAIL_SORT_FOLDER_ORDER, sortType.ordinal) }
    }

    override fun setDetailPlaylistSort(sortType: Sort.Type) {
        return preferences.edit { putInt(DETAIL_SORT_PLAYLIST_ORDER, sortType.ordinal) }
    }

    override fun setDetailAlbumSort(sortType: Sort.Type) {
        return preferences.edit { putInt(DETAIL_SORT_ALBUM_ORDER, sortType.ordinal) }
    }

    override fun setDetailArtistSort(sortType: Sort.Type) {
        return preferences.edit { putInt(DETAIL_SORT_ARTIST_ORDER, sortType.ordinal) }
    }

    override fun setDetailGenreSort(sortType: Sort.Type) {
        return preferences.edit { putInt(DETAIL_SORT_GENRE_ORDER, sortType.ordinal) }
    }

    override fun toggleDetailSortArranging(category: MediaIdCategory) {
        val arrangingKey = when (category){
            MediaIdCategory.FOLDERS -> DETAIL_SORT_FOLDER_ARRANGING
            MediaIdCategory.ALBUMS -> DETAIL_SORT_ALBUM_ARRANGING
            MediaIdCategory.ARTISTS -> DETAIL_SORT_ARTIST_ARRANGING
            MediaIdCategory.GENRES -> DETAIL_SORT_GENRE_ARRANGING
            MediaIdCategory.PLAYLISTS -> DETAIL_SORT_PLAYLIST_ARRANGING
            else -> throw IllegalStateException("invalid category $category")
        }

        val oldArranging = Sort.Arranging.values()[preferences.getInt(
            arrangingKey,
            Sort.Arranging.ASCENDING.ordinal
        )]

        val newArranging = if (oldArranging == Sort.Arranging.ASCENDING) {
            Sort.Arranging.DESCENDING
        } else Sort.Arranging.ASCENDING

        preferences.edit {
            putInt(arrangingKey, newArranging.ordinal)
        }
    }

    override fun getDetailFolderSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_FOLDER_ORDER, Sort.Type.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_FOLDER_ARRANGING, Sort.Arranging.ASCENDING.ordinal)
        return Sort(
            Sort.Type.values()[order],
            Sort.Arranging.values()[arranging]
        )
    }

    override fun getDetailPlaylistSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_PLAYLIST_ORDER, Sort.Type.CUSTOM.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_PLAYLIST_ARRANGING, Sort.Arranging.ASCENDING.ordinal)
        return Sort(
            Sort.Type.values()[order],
            Sort.Arranging.values()[arranging]
        )
    }

    override fun getDetailAlbumSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_ALBUM_ORDER, Sort.Type.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ALBUM_ARRANGING, Sort.Arranging.ASCENDING.ordinal)
        return Sort(
            Sort.Type.values()[order],
            Sort.Arranging.values()[arranging]
        )
    }

    override fun getDetailArtistSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_ARTIST_ORDER, Sort.Type.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ARTIST_ARRANGING, Sort.Arranging.ASCENDING.ordinal)
        return Sort(
            Sort.Type.values()[order],
            Sort.Arranging.values()[arranging]
        )
    }

    override fun getDetailGenreSort(): Sort {
        val order = preferences.getInt(DETAIL_SORT_GENRE_ORDER, Sort.Type.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_GENRE_ARRANGING, Sort.Arranging.ASCENDING.ordinal)
        return Sort(
            Sort.Type.values()[order],
            Sort.Arranging.values()[arranging]
        )
    }

}