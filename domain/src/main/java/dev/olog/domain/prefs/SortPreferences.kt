package dev.olog.domain.prefs

import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import kotlinx.coroutines.flow.Flow

interface SortPreferences: SortAll, SortDetail

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