package dev.olog.core.prefs

import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import io.reactivex.Completable
import io.reactivex.Observable

interface SortPreferences {

    fun getAllTracksSortOrder(): SortEntity
    fun getAllAlbumsSortOrder(): SortEntity
    fun getAllArtistsSortOrder(): SortEntity

    fun observeAllTracksSortOrder(): Observable<SortEntity>
    fun observeAllAlbumsSortOrder(): Observable<SortEntity>
    fun observeAllArtistsSortOrder(): Observable<SortEntity>

    fun setAllTracksSortOrder(sortType: SortEntity)
    fun setAllAlbumsSortOrder(sortType: SortEntity)
    fun setAllArtistsSortOrder(sortType: SortEntity)

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