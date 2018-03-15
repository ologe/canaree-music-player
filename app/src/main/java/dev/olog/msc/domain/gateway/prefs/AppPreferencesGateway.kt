package dev.olog.msc.domain.gateway.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
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
    fun setSleepTimer(millis: Long)
    fun getSleepTimer() : Long

    fun observeMiniQueueVisibility(): Observable<Boolean>

    fun observePlayerControlsVisibility(): Observable<Boolean>

    fun setCanDownloadOnMobile(can: Boolean)
    fun getCanDownloadOnMobile() : Boolean
    fun observeCanDownloadOnMobile(): Observable<Boolean>

    fun canShowTurnOnWifiMessageForImages(): Observable<Boolean>
    fun hideTurnOnWifiMessageForImages()

}

interface Sorting {

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