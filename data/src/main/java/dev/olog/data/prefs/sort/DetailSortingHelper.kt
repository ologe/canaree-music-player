package dev.olog.data.prefs.sort

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortDetail
import dev.olog.data.utils.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DetailSortingHelper @Inject constructor(
    private val preferences: SharedPreferences
) : SortDetail {

    companion object {
        private const val TAG = AppSortingImpl.TAG
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

    override fun observeDetailFolderSort(): Flow<SortEntity> {
        return preferences.observeKey(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_FOLDER_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                SortEntity(
                    SortType.values()[type],
                    SortArranging.values()[arranging]
                )
            }
    }

    override fun observeDetailPlaylistSort(): Flow<SortEntity> {
        return preferences.observeKey(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_PLAYLIST_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                SortEntity(
                    SortType.values()[type],
                    SortArranging.values()[arranging]
                )
            }
    }

    override fun observeDetailAlbumSort(): Flow<SortEntity> {
        return preferences.observeKey(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ALBUM_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                SortEntity(
                    SortType.values()[type],
                    SortArranging.values()[arranging]
                )
            }
    }

    override fun observeDetailArtistSort(): Flow<SortEntity> {
        return preferences.observeKey(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_ARTIST_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                SortEntity(
                    SortType.values()[type],
                    SortArranging.values()[arranging]
                )
            }
    }

    override fun observeDetailGenreSort(): Flow<SortEntity> {
        return preferences.observeKey(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
            .combine(
                preferences.observeKey(
                    DETAIL_SORT_GENRE_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                )
            ) { type, arranging ->
                SortEntity(
                    SortType.values()[type],
                    SortArranging.values()[arranging]
                )
            }
    }

    override fun setDetailFolderSort(sortType: SortType) {
        return preferences.edit { putInt(DETAIL_SORT_FOLDER_ORDER, sortType.ordinal) }
    }

    override fun setDetailPlaylistSort(sortType: SortType) {
        return preferences.edit { putInt(DETAIL_SORT_PLAYLIST_ORDER, sortType.ordinal) }
    }

    override fun setDetailAlbumSort(sortType: SortType) {
        return preferences.edit { putInt(DETAIL_SORT_ALBUM_ORDER, sortType.ordinal) }
    }

    override fun setDetailArtistSort(sortType: SortType) {
        return preferences.edit { putInt(DETAIL_SORT_ARTIST_ORDER, sortType.ordinal) }
    }

    override fun setDetailGenreSort(sortType: SortType) {
        return preferences.edit { putInt(DETAIL_SORT_GENRE_ORDER, sortType.ordinal) }
    }

    override fun toggleDetailSortArranging(category: MediaIdCategory) {
        val arrangingKey = when (category){
            MediaIdCategory.FOLDERS -> DETAIL_SORT_FOLDER_ARRANGING
            MediaIdCategory.PLAYLISTS -> DETAIL_SORT_PLAYLIST_ARRANGING
            MediaIdCategory.ALBUMS -> DETAIL_SORT_ALBUM_ARRANGING
            MediaIdCategory.ARTISTS -> DETAIL_SORT_ARTIST_ARRANGING
            MediaIdCategory.GENRES -> DETAIL_SORT_GENRE_ARRANGING
            else -> throw IllegalStateException("invalid category $category")
        }

        val oldArranging = SortArranging.values()[preferences.getInt(
            arrangingKey,
            SortArranging.ASCENDING.ordinal
        )]

        val newArranging = if (oldArranging == SortArranging.ASCENDING) {
            SortArranging.DESCENDING
        } else SortArranging.ASCENDING

        preferences.edit {
            putInt(arrangingKey, newArranging.ordinal)
        }
    }

    override fun getDetailFolderSort(): SortEntity {
        val order = preferences.getInt(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_FOLDER_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortEntity(
            SortType.values()[order],
            SortArranging.values()[arranging]
        )
    }

    override fun getDetailPlaylistSort(): SortEntity {
        val order = preferences.getInt(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_PLAYLIST_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortEntity(
            SortType.values()[order],
            SortArranging.values()[arranging]
        )
    }

    override fun getDetailAlbumSort(): SortEntity {
        val order = preferences.getInt(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ALBUM_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortEntity(
            SortType.values()[order],
            SortArranging.values()[arranging]
        )
    }

    override fun getDetailArtistSort(): SortEntity {
        val order = preferences.getInt(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_ARTIST_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortEntity(
            SortType.values()[order],
            SortArranging.values()[arranging]
        )
    }

    override fun getDetailGenreSort(): SortEntity {
        val order = preferences.getInt(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(DETAIL_SORT_GENRE_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortEntity(
            SortType.values()[order],
            SortArranging.values()[arranging]
        )
    }

}