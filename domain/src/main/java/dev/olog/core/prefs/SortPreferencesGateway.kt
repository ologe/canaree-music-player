package dev.olog.core.prefs

import dev.olog.core.MediaIdCategory
import dev.olog.core.ResettablePreference
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import kotlinx.coroutines.flow.Flow

interface SortPreferencesGateway: ResettablePreference, SortAll, SortDetail

// TODO sorting for podcast?
// TODO create some sealed class to distinguish all from detail categories
interface SortAll {
    fun getAllTracksSort(): SortEntity
    fun getAllAlbumsSort(): SortEntity
    fun getAllArtistsSort(): SortEntity

    fun setAllTracksSort(sortType: SortEntity)
    fun setAllAlbumsSort(sortType: SortEntity)
    fun setAllArtistsSort(sortType: SortEntity)
}

interface SortDetail {
    fun observeDetailFolderSort() : Flow<SortEntity>
    fun observeDetailPlaylistSort() : Flow<SortEntity>
    fun observeDetailAlbumSort() : Flow<SortEntity>
    fun observeDetailArtistSort() : Flow<SortEntity>
    fun observeDetailGenreSort() : Flow<SortEntity>

    fun getDetailFolderSort() : SortEntity
    fun getDetailPlaylistSort() : SortEntity
    fun getDetailAlbumSort() : SortEntity
    fun getDetailArtistSort() : SortEntity
    fun getDetailGenreSort() : SortEntity

    fun setDetailFolderSort(sortType: SortType)
    fun setDetailPlaylistSort(sortType: SortType)
    fun setDetailAlbumSort(sortType: SortType)
    fun setDetailArtistSort(sortType: SortType)
    fun setDetailGenreSort(sortType: SortType)

    fun toggleDetailSortArranging(category: MediaIdCategory)
}