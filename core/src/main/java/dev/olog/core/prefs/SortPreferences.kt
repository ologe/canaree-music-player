package dev.olog.core.prefs

import dev.olog.core.entity.sort.LibrarySortType
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import io.reactivex.Completable
import io.reactivex.Observable

interface SortPreferences {

    fun getAllTracksSortOrder(): LibrarySortType
    fun getAllAlbumsSortOrder(): LibrarySortType
    fun getAllArtistsSortOrder(): LibrarySortType

    fun observeAllTracksSortOrder(): Observable<LibrarySortType>
    fun observeAllAlbumsSortOrder(): Observable<LibrarySortType>
    fun observeAllArtistsSortOrder(): Observable<LibrarySortType>

    fun setAllTracksSortOrder(sortType: LibrarySortType)
    fun setAllAlbumsSortOrder(sortType: LibrarySortType)
    fun setAllArtistsSortOrder(sortType: LibrarySortType)

    fun observeDetailFolderSortOrder() : Observable<SortType>
    fun observeDetailPlaylistSortOrder() : Observable<SortType>
    fun observeDetailAlbumSortOrder() : Observable<SortType>
    fun observeDetailArtistSortOrder() : Observable<SortType>
    fun observeDetailGenreSortOrder() : Observable<SortType>

    fun getDetailFolderSortOrder() : SortType
    fun getDetailPlaylistSortOrder() : SortType
    fun getDetailAlbumSortOrder() : SortType
    fun getDetailArtistSortOrder() : SortType
    fun getDetailGenreSortOrder() : SortType

    fun setDetailFolderSortOrder(sortType: SortType) : Completable
    fun setDetailPlaylistSortOrder(sortType: SortType) : Completable
    fun setDetailAlbumSortOrder(sortType: SortType) : Completable
    fun setDetailArtistSortOrder(sortType: SortType) : Completable
    fun setDetailGenreSortOrder(sortType: SortType) : Completable

    fun observeDetailSortArranging(): Observable<SortArranging>
    fun getDetailSortArranging(): SortArranging
    fun toggleSortArranging(): Completable
}