package dev.olog.msc.domain.gateway.prefs

import dev.olog.msc.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Observable

interface AppPreferencesGateway : Sorting {

    fun isFirstAccess(): Boolean

    fun getVisibleTabs(): Observable<BooleanArray>

    fun getViewPagerLastVisitedPage(): Int
    fun setViewPagerLastVisitedPage(lastPage: Int)

    fun getLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior>
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>)

    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun resetSleepTimer()
    fun setSleepTimer(sleepFrom: Long, sleepTime: Long)
    fun getSleepTime() : Long
    fun getSleepFrom() : Long

    fun observePlayerControlsVisibility(): Observable<Boolean>

    fun setDefault(): Completable

    fun observeAutoCreateImages(): Observable<Boolean>

    fun getLastFmCredentials(): UserCredendials
    fun observeLastFmCredentials(): Observable<UserCredendials>
    fun setLastFmCredentials(user: UserCredendials)

    fun getSyncAdjustment(): Long
    fun setSyncAdjustment(value: Long)

}

interface Sorting {

    fun getAllTracksSortOrder(): LibrarySortType
    fun getAllAlbumsSortOrder(): LibrarySortType

    fun observeAllTracksSortOrder(): Observable<LibrarySortType>
    fun observeAllAlbumsSortOrder(): Observable<LibrarySortType>

    fun setAllTracksSortOrder(sortType: LibrarySortType)
    fun setAllAlbumsSortOrder(sortType: LibrarySortType)

    fun getFolderSortOrder() : Observable<SortType>
    fun getPlaylistSortOrder() : Observable<SortType>
    fun getAlbumSortOrder() : Observable<SortType>
    fun getArtistSortOrder() : Observable<SortType>
    fun getGenreSortOrder() : Observable<SortType>

    fun setFolderSortOrder(sortType: SortType) : Completable
    fun setPlaylistSortOrder(sortType: SortType) : Completable
    fun setAlbumSortOrder(sortType: SortType) : Completable
    fun setArtistSortOrder(sortType: SortType) : Completable
    fun setGenreSortOrder(sortType: SortType) : Completable

    fun getSortArranging(): Observable<SortArranging>
    fun toggleSortArranging(): Completable

}