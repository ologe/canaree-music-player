package dev.olog.domain.prefs

import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.ResettablePreference
import dev.olog.domain.entity.Sort
import kotlinx.coroutines.flow.Flow

interface SortPreferencesGateway: ResettablePreference, SortAll, SortDetail

// TODO sorting for podcast?
// TODO create some sealed class to distinguish all from detail categories
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

    fun setDetailFolderSort(sortType: Sort.Type)
    fun setDetailPlaylistSort(sortType: Sort.Type)
    fun setDetailAlbumSort(sortType: Sort.Type)
    fun setDetailArtistSort(sortType: Sort.Type)
    fun setDetailGenreSort(sortType: Sort.Type)

    fun toggleDetailSortArranging(category: MediaIdCategory)
}