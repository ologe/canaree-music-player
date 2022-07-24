package dev.olog.core.prefs

import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortType
import kotlinx.coroutines.flow.Flow

interface SortPreferences: SortAll, SortDetail

interface SortAll {
    fun getAllTracksSort(): Sort
    fun getAllAlbumsSort(): Sort
    fun getAllArtistsSort(): Sort

    fun setAllTracksSort(sortType: Sort)
    fun setAllAlbumsSort(sortType: Sort)
    fun setAllArtistsSort(sortType: Sort)
}

interface SortDetail {
    fun observeDetailFolderSort() : Flow<Sort>
    fun observeDetailPlaylistSort() : Flow<Sort>
    fun observeDetailAlbumSort() : Flow<Sort>
    fun observeDetailArtistSort() : Flow<Sort>
    fun observeDetailGenreSort() : Flow<Sort>

    fun getDetailFolderSort() : Sort
    fun getDetailPlaylistSort() : Sort
    fun getDetailAlbumSort() : Sort
    fun getDetailArtistSort() : Sort
    fun getDetailGenreSort() : Sort

    fun setDetailFolderSort(sortType: SortType)
    fun setDetailPlaylistSort(sortType: SortType)
    fun setDetailAlbumSort(sortType: SortType)
    fun setDetailArtistSort(sortType: SortType)
    fun setDetailGenreSort(sortType: SortType)

    fun toggleDetailSortArranging(category: MediaIdCategory)
}